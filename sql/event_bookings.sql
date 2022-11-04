DROP TABLE IF EXISTS event_bookings;

CREATE TABLE event_bookings (
    booking_id VARCHAR(50) NOT NULL,
    event_id int NOT NULL,
    user_id bigint NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (booking_id),
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
)
