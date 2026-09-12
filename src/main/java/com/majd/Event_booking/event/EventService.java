package com.majd.Event_booking.event;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.majd.Event_booking.event.dto.CreateEventRequst;
import com.majd.Event_booking.event.dto.EventResponse;
import com.majd.Event_booking.event.dto.UpdateEventRequest;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventResponse createEvent(CreateEventRequst request) {
        Event event = new Event(
                request.name(),
                request.city(),
                request.capacity(),
                request.startsAt()
        );

        Event savedEvent = eventRepository.save(event);

        return toResponse(savedEvent);
    }

    public Page<EventResponse> getEvents(String city, Pageable pageable) {
        Page<Event> events;

        if (city == null || city.isBlank()) {
            events = eventRepository.findAll(pageable);
        } else {
            events = eventRepository.findByCityIgnoreCase(city, pageable);
        }

        return events.map(this::toResponse);
    }

    public Optional<EventResponse> getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .map(this::toResponse);
    }

    public Optional<EventResponse> updateEvent(
            long eventId,
            UpdateEventRequest request
    ) {
        return eventRepository.findById(eventId)
                .map(event -> {
                    event.update(
                            request.name(),
                            request.city(),
                            request.capacity(),
                            request.startsAt()

                    );

                    Event savedEvent = eventRepository.save(event);
                    return toResponse(savedEvent);
                });
    }

    public boolean deleteEvent(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            return false;
        }

        eventRepository.deleteById(eventId);
        return true;
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getCity(),
                event.getCapacity(),
                event.getStartsAt()

        );
    }
}