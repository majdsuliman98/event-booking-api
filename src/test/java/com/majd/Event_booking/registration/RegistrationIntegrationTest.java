package com.majd.Event_booking.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.majd.Event_booking.event.Event;
import com.majd.Event_booking.event.EventRepository;
import com.majd.Event_booking.registration.dto.CreateRegistrationRequest;
import com.majd.Event_booking.registration.dto.RegistrationResponse;

@SpringBootTest
@Testcontainers
class RegistrationIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
        new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private RegistrationService registrationService;

    @BeforeEach
    void cleanDatabase() {
        registrationRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    void waitlistsRegistrationWhenEventIsFull() {
        Event event = eventRepository.save(new Event(
                "Java Meetup",
                "Krakow",
                1,
                Instant.parse("2030-12-15T18:00:00Z")
        ));

        RegistrationResponse first =
                registrationService.register(
                        event.getId(),
                        new CreateRegistrationRequest(
                                "Alice",
                                "alice@example.com"
                        )
                );

        RegistrationResponse second =
                registrationService.register(
                        event.getId(),
                        new CreateRegistrationRequest(
                                "Bob",
                                "bob@example.com"
                        )
                );

        assertEquals(
                RegistrationStatus.CONFIRMED,
                first.status()
        );

        assertEquals(
                RegistrationStatus.WAITLISTED,
                second.status()
        );
    }

   @Test
    void confirmsOnlyOneRegistrationWhenLastPlaceIsRequestedConcurrently()
            throws Exception {

        Event event = eventRepository.save(new Event(
                "Java Meetup",
                "Krakow",
                1,
                Instant.parse("2030-12-15T18:00:00Z")
        ));

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<RegistrationResponse> registerAlice = () -> {
            ready.countDown();
            start.await();

            return registrationService.register(
                    event.getId(),
                    new CreateRegistrationRequest(
                            "Alice",
                            "alice.concurrent@example.com"
                    )
            );
        };

        Callable<RegistrationResponse> registerBob = () -> {
            ready.countDown();
            start.await();

            return registrationService.register(
                    event.getId(),
                    new CreateRegistrationRequest(
                            "Bob",
                            "bob.concurrent@example.com"
                    )
            );
        };

        try {
            Future<RegistrationResponse> aliceFuture =
                    executor.submit(registerAlice);

            Future<RegistrationResponse> bobFuture =
                    executor.submit(registerBob);

            assertTrue(ready.await(5, TimeUnit.SECONDS));

            start.countDown();

            RegistrationResponse alice =
                    aliceFuture.get(10, TimeUnit.SECONDS);

            RegistrationResponse bob =
                    bobFuture.get(10, TimeUnit.SECONDS);

            List<RegistrationStatus> statuses =
                    List.of(alice.status(), bob.status());

            long confirmedCount = statuses.stream()
                    .filter(status ->
                            status == RegistrationStatus.CONFIRMED)
                    .count();

            long waitlistedCount = statuses.stream()
                .filter(status ->
                        status == RegistrationStatus.WAITLISTED)
                .count();

        assertEquals(1, confirmedCount);
        assertEquals(1, waitlistedCount);
    } finally {
        executor.shutdownNow();
    }
}

}

