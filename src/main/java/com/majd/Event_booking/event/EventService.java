package com.majd.Event_booking.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class EventService {
    private long nextId = 1;
    private List<EventResponse> events = new ArrayList<>();

    public EventResponse createEvent(CreateEventRequst request) {

        EventResponse event = new EventResponse(
                nextId,
                request.city(),
                request.name(),
                request.capacity());
        events.add(event);
        nextId++;
        return event;

    }

    public List<EventResponse> getEvents(String city) {
        if(city == null || city.isBlank()) {
            System.out.println("Heree");
            return List.copyOf(events);
        }

        List<EventResponse> result = new ArrayList<>();
        for (EventResponse event : events){
            if(event.city().equalsIgnoreCase(city)) {
                System.out.println(event);
                result.add(event);
            }
        }




        return result;
    }

    public Optional<EventResponse> getEvent(long eventId) {
        for(EventResponse event : events) {
            if(event.id() == eventId) {
                return Optional.of(event);
            }
        }
        return Optional.empty();
    }

    public Optional<EventResponse> updateEvent(long eventId, UpdateEventRequest request) {

        for(int index=0; index < events.size(); index++) {
            EventResponse event = events.get(index);
            if(event.id() == eventId) {
                EventResponse updatedEvent = new EventResponse(
                        eventId,
                        request.name(),
                        request.city(),
                        request.capacity());
                        
                events.set(index, updatedEvent);
                return Optional.of(updatedEvent);
            }
        }
        return Optional.empty();
        
    }

    public boolean deleteEvent(long eventId) {
        return events.removeIf(event -> event.id() == eventId);
    }

}
