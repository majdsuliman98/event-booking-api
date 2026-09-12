CREATE TABLE registrations (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    attendee_name VARCHAR(255) NOT NULL,
    attendee_email VARCHAR(255) NOT NULL,
    registered_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_registrations_event
        FOREIGN KEY (event_id)
        REFERENCES events(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_registrations_event_email
        UNIQUE (event_id, attendee_email),

    CONSTRAINT chk_registrations_status
        CHECK (status IN ('CONFIRMED', 'WAITLISTED'))
);

CREATE INDEX idx_registrations_event_id
ON registrations(event_id);