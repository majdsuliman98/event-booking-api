package com.majd.Event_booking.registration;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.majd.Event_booking.common.error.ConflictException;
import com.majd.Event_booking.common.error.NotFoundException;
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
                .orElseThrow(() -> new NotFoundException("Event not found"));

        boolean alreadyRegistered =
                registrationRepository
                        .existsByEventIdAndAttendeeEmailIgnoreCase(
                                eventId,
                                request.attendeeEmail()
                        );

        if (alreadyRegistered) {
            throw new ConflictException(
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

    @Transactional
    public void cancelRegistration(long eventId, long registrationId) {
        eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        Registration registration = registrationRepository
                .findByIdAndEventId(registrationId, eventId)
                .orElseThrow(() -> new NotFoundException("Registration not found"));

        Boolean confirmed = registration.getStatus() == RegistrationStatus.CONFIRMED;
        
        registrationRepository.delete(registration);

        if(confirmed){
                registrationRepository.findFirstByEventIdAndStatusOrderByRegisteredAtAsc(eventId, RegistrationStatus.WAITLISTED).ifPresent(Registration::confirm);
        }

    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRegistrations(long eventId) {
        if(!eventRepository.existsById(eventId)){
            throw new NotFoundException("Event not found");
        }
        return registrationRepository.findByEventId(eventId)
                .stream()
                .map(this::toResponse)
                .toList();
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
