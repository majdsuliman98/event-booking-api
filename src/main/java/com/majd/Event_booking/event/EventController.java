package com.majd.Event_booking.event;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.majd.Event_booking.common.error.NotFoundException;
import com.majd.Event_booking.event.dto.CreateEventRequst;
import com.majd.Event_booking.event.dto.EventResponse;
import com.majd.Event_booking.event.dto.UpdateEventRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
@Tag(
        name = "Events",
        description = "Create and manage bookable events"
)
@RestController
@RequestMapping("/api/events")
public class EventController {
    private final Logger log = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(
        summary = "Get an event",
        description = "Returns one event using its database ID"
    )
    @GetMapping("/{eventId}")
    public EventResponse getEvent(
            @PathVariable long eventId) {
        return eventService.getEvent(eventId).orElseThrow(() -> new NotFoundException("Event not found"));

    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody CreateEventRequst request) {
        return eventService.createEvent(request);
    }


    @GetMapping
    public Page<EventResponse> getEvents(@RequestParam(required = false) String city, Pageable pageable) {
        log.debug(
            "Fetching events: city={}, page={}, size={}",
            city,
            pageable.getPageNumber(),
            pageable.getPageSize()
        );

        return eventService.getEvents(city, pageable);
    }

    @PutMapping("/{eventId}")
    public EventResponse updateEvent(@PathVariable long eventId, @Valid @RequestBody UpdateEventRequest request) {
        return eventService.updateEvent(eventId, request)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable long eventId) {
        boolean isDeleted = eventService.deleteEvent(eventId);

        if(!isDeleted) {
            throw new NotFoundException("Event not found");
        }


    }
}
