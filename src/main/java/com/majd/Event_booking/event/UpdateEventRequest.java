package com.majd.Event_booking.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;


public record UpdateEventRequest( 
    
    @NotBlank(message = "Event name is required")
    String name,
    
    @NotBlank(message = "Event city is required")
    String city,

    @Positive(message = "Capacity must be a positive number")
    int capacity
) {
}
