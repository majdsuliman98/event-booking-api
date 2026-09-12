package com.majd.Event_booking.event.dto;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record UpdateEventRequest( 
    
    @NotBlank(message = "Event name is required")
    String name,
    
    @NotBlank(message = "Event city is required")
    String city,

    @Positive(message = "Capacity must be a positive number")
    int capacity,
    
    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    Instant startsAt
) {
}
