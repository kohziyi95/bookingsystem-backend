package com.vttp.bookingsystembackend.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class EventBooking {
    String bookingId;
    Integer eventId;
    Integer userId;

    public static EventBooking createEvent(SqlRowSet rowSet) {
        EventBooking e = new EventBooking();
        e.setBookingId(rowSet.getString("booking_id"));
        e.setEventId(rowSet.getInt("event_id"));
        e.setUserId(rowSet.getInt("user_id"));
        return e;
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("bookingId", bookingId)
                .add("eventId", eventId)
                .add("userId", userId);
        return builder.build();
    }

    public String getBookingId() {
        return bookingId;
    }
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
    public Integer getEventId() {
        return eventId;
    }
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    
}
