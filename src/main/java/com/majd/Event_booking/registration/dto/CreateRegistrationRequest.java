package com.majd.Event_booking.registration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateRegistrationRequest(

        @NotBlank(message = "Attendee name is required")
        String attendeeName,

        @NotBlank(message = "Attendee email is required")
        @Email(message = "Attendee email must be valid")
        String attendeeEmail
) {
}
