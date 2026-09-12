package com.majd.Event_booking.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private int capacity;

    private Instant startsAt;

    protected Event() {
    }

    public Event(String name, String city, int capacity, Instant startsAt) {
        this.name = name;
        this.city = city;
        this.capacity = capacity;
        this.startsAt = startsAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public int getCapacity() {
        return capacity;
    }

    public Instant getStartsAt() {
        return startsAt;
    }
    public void update(String name, String city, int capacity, Instant startsAt) {
        this.name = name;
        this.city = city;
        this.capacity = capacity;
        this.startsAt = startsAt;
    }
}