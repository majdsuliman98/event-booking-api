package com.majd.Event_booking.registration;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository
        extends JpaRepository<Registration, Long> {

        List<Registration> findByEventId(long eventId);

        boolean existsByEventIdAndAttendeeEmailIgnoreCase(
            long eventId,
            String attendeeEmail
        );

        long countByEventIdAndStatus(
            long eventId,
            RegistrationStatus status
        );
        
        Optional<Registration> findByIdAndEventId(
        long registrationId,
        long eventId
);

        Optional<Registration> findFirstByEventIdAndStatusOrderByRegisteredAtAsc(
                long eventId,
                RegistrationStatus status
        );

}