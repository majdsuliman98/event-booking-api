package com.majd.Event_booking.registration.dto;


import java.time.Instant;

import com.majd.Event_booking.registration.RegistrationStatus;

public record RegistrationResponse(
        long id,
        long eventId,
        String attendeeName,
        String attendeeEmail,
        Instant registeredAt,
        RegistrationStatus status
) {
}