package com.majd.Event_booking.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.majd.Event_booking.common.error.ConflictException;
import com.majd.Event_booking.common.error.NotFoundException;
import com.majd.Event_booking.event.Event;
import com.majd.Event_booking.event.EventRepository;
import com.majd.Event_booking.registration.dto.CreateRegistrationRequest;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void rejectsDuplicateEmail() {
        long eventId = 1;

        Event event = new Event(
                "Java Meetup",
                "Krakow",
                10,
                Instant.parse("2030-12-15T18:00:00Z")
        );

        CreateRegistrationRequest request =
                new CreateRegistrationRequest(
                        "Alice",
                        "alice@example.com"
                );

        when(eventRepository.findByIdForUpdate(eventId))
                .thenReturn(Optional.of(event));

        when(registrationRepository
                .existsByEventIdAndAttendeeEmailIgnoreCase(
                        eventId,
                        request.attendeeEmail()
                ))
                .thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> registrationService.register(eventId, request)
        );

        assertEquals(
                "Email is already registered for this event",
                exception.getMessage()
        );

        verify(registrationRepository, never())
                .save(any(Registration.class));
    }

    @Test
    void rejectsRegistrationWhenEventDoesNotExist(){
        long eventId = 999;

        CreateRegistrationRequest request =
                new CreateRegistrationRequest(
                        "Alice",
                        "alice@example.com"
                );

        when(eventRepository.findByIdForUpdate(eventId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> registrationService.register(eventId, request));

        assertEquals("Event not found", exception.getMessage());
        
        verifyNoInteractions(registrationRepository);

        
    }
}
