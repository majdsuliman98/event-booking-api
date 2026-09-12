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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> registrationService.register(eventId, request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

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

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> registrationService.register(eventId, request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        
        verifyNoInteractions(registrationRepository);

        
    }
}