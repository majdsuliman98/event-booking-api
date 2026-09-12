package com.majd.Event_booking.event.dto;

import java.time.Instant;

public record EventResponse(
        long id,
        String name,
        String city,
        int capacity,
        Instant startsAt
) {

}
