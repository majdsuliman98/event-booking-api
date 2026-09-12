package com.majd.Event_booking.event.dto;

import java.time.Instant;

public record EventResponse(
        long id,
        String city,
        String name,
        int capacity,
        Instant startsAt
) {

}
