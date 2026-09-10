package com.majd.Event_booking.event;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @GetMapping("/{eventId}")
    public EventResponse getEvent(
            @PathVariable long eventId) {
        return eventService.getEvent(eventId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Event not found"
        ));

    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody CreateEventRequst request) {
        return eventService.createEvent(request);
    }


    @GetMapping
    public List<EventResponse> getEvents(@RequestParam(required = false) String city) {
        System.out.println("Selected city: "+ city);
        return eventService.getEvents(city);
    }

    @PutMapping("/{eventId}")
    public EventResponse updateEvent(@PathVariable long eventId, @Valid @RequestBody UpdateEventRequest request) {
        return eventService.updateEvent(eventId, request).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Event not found"
        ));
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable long eventId) {
        boolean isDeleted = eventService.deleteEvent(eventId);

        if(!isDeleted) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Event not found"
            );
        }


    }
}
