package com.majd.Event_booking.event;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface EventRepository extends JpaRepository<Event, Long> {

        Page<Event> findByCityIgnoreCase(String city, Pageable pageable);    
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("""
                SELECT event
                FROM Event event
                WHERE event.id = :eventId
                """)
        Optional<Event> findByIdForUpdate(
                @Param("eventId") long eventId
        );
    
}