package com.majd.Event_booking.registration;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.majd.Event_booking.event.Event;
import com.majd.Event_booking.event.EventRepository;
import com.majd.Event_booking.registration.dto.CreateRegistrationRequest;
import com.majd.Event_booking.registration.dto.RegistrationResponse;

@Service
public class RegistrationService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public RegistrationService(
            EventRepository eventRepository,
            RegistrationRepository registrationRepository
    ) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    @Transactional
    public RegistrationResponse register(
            long eventId,
            CreateRegistrationRequest request
    ) {
        Event event = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event not found"
                ));

        boolean alreadyRegistered =
                registrationRepository
                        .existsByEventIdAndAttendeeEmailIgnoreCase(
                                eventId,
                                request.attendeeEmail()
                        );

        if (alreadyRegistered) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered for this event"
            );
        }

        long confirmedCount =
                registrationRepository.countByEventIdAndStatus(
                        eventId,
                        RegistrationStatus.CONFIRMED
                );

        RegistrationStatus status =
                confirmedCount < event.getCapacity()
                        ? RegistrationStatus.CONFIRMED
                        : RegistrationStatus.WAITLISTED;

        Registration registration = new Registration(
                event,
                request.attendeeName(),
                request.attendeeEmail(),
                status
        );

        Registration savedRegistration =
                registrationRepository.save(registration);

        return toResponse(savedRegistration);
    }

    private RegistrationResponse toResponse(
            Registration registration
    ) {
        return new RegistrationResponse(
                registration.getId(),
                registration.getEvent().getId(),
                registration.getAttendeeName(),
                registration.getAttendeeEmail(),
                registration.getRegisteredAt(),
                registration.getStatus()
        );
    }
}