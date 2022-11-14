package com.vttp.bookingsystembackend.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.vttp.bookingsystembackend.models.EventBooking;
import com.vttp.bookingsystembackend.models.EventDetails;

@Repository
public class EventRepository {
    private Logger logger = Logger.getLogger(EventRepository.class.getName());

    @Autowired
    private JdbcTemplate template;

    public static final String SQL_GET_IMAGE_BY_ID = "select image from events where id = ?";
    public static final String SQL_DELETE_EVENT_BY_ID = "delete from events where id = ?";
    public static final String SQL_GET_EVENT_BY_ID = "select * from events where id = ?";
    public static final String SQL_GET_EVENT_CAPACITY = "select capacity from events where id = ?";
    public static final String SQL_GET_EVENT_BY_BOOKING_ID = "select * from event_bookings join events on event_bookings.event_id = events.id where booking_id = ?";
    public static final String SQL_GET_LATEST_EVENT_BY_TITLE = "select * from events where title = ? order by date_created desc limit 1";

    public static final String SQL_INSERT_BOOKING = "insert into event_bookings(booking_id, user_id, event_id) values (? ,? ,?)";
    public static final String SQL_GET_BOOKING_COUNT_BY_EVENT_ID = "select count(*) as count from event_bookings where event_id = ?";
    public static final String SQL_GET_BOOKING_COUNT_BY_USER_ID_AND_EVENT_ID = "select count(*) as count from event_bookings where user_id = ? and event_id = ? ";
    public static final String SQL_DELETE_BOOKINGS_BY_EVENT_ID = "delete from event_bookings where event_id = ?";
    public static final String SQL_GET_BOOKINGS_BY_USER_ID = "select * from event_bookings where user_id = ?";
    public static final String SQL_GET_BOOKINGS_BY_EVENT_ID = "select * from event_bookings where event_id = ?";
    public static final String SQL_GET_BOOKING_BY_BOOKING_ID = "select * from event_bookings where booking_id = ?";
    public static final String SQL_DELETE_BOOKINGS_BY_BOOKING_ID = "delete from event_bookings where booking_id = ?";

    public List<EventDetails> getEvents(String SQL) throws Exception {
        List<EventDetails> eventList = new ArrayList<>();
        SqlRowSet rowSet = template.queryForRowSet(SQL);
        while (rowSet.next()) {
            EventDetails event = EventDetails.createEvent(rowSet);
            byte[] image = getImageById(event.getId()).get();
            // System.out.println("Getting image >>> " + image);
            event.setImage(image);
            eventList.add(event);
            event.setBookingCount(getBookingCount(event.getId()));
        }
        return eventList;
    }



    public Optional<byte[]> getImageById(Integer id) throws Exception {
        return template.query(SQL_GET_IMAGE_BY_ID,
                (ResultSet rs) -> {
                    if (!rs.next())
                        return Optional.empty();
                    return Optional.of(rs.getBytes("image"));
                },
                id);
    }

    public Integer deleteEvents(Integer id) {
        return template.update(SQL_DELETE_EVENT_BY_ID, id);
    }

    public EventDetails getEventById(Integer id) throws Exception {
        // EventDetails e = template.queryForObject(SQL_GET_EVENT_BY_ID,
        // EventDetails.class);
        // Optional<byte[]> opt = getImageById(id);
        // if (opt.isEmpty()) {
        // throw new Exception();
        // } else {
        // byte[] image = opt.get();
        // e.setImage(image);
        // }
        EventDetails event = new EventDetails();
        SqlRowSet rowSet = template.queryForRowSet(SQL_GET_EVENT_BY_ID, id);
        while (rowSet.next()) {
            event = EventDetails.createEvent(rowSet);
            byte[] image = getImageById(event.getId()).get();
            // System.out.println("Getting image >>> " + image);
            event.setImage(image);
            event.setBookingCount(getBookingCount(event.getId()));
        }
        return event;
    }

    public Integer getEventCapacity(Integer eventId) {
        Integer capacity = 0;
        SqlRowSet rowSet = template.queryForRowSet(SQL_GET_EVENT_CAPACITY, eventId);
        while (rowSet.next()) {
            capacity = rowSet.getInt("capacity");
        }
        return capacity;
    }

    public EventDetails getLatestEventByTitle(String title) throws Exception {
        // EventDetails e = template.queryForObject(SQL_GET_EVENT_BY_ID,
        // EventDetails.class);
        // Optional<byte[]> opt = getImageById(id);
        // if (opt.isEmpty()) {
        // throw new Exception();
        // } else {
        // byte[] image = opt.get();
        // e.setImage(image);
        // }
        EventDetails event = new EventDetails();
        SqlRowSet rowSet = template.queryForRowSet(SQL_GET_LATEST_EVENT_BY_TITLE, title);
        while (rowSet.next()) {
            event = EventDetails.createEvent(rowSet);
            byte[] image = getImageById(event.getId()).get();
            // System.out.println("Getting image >>> " + image);
            event.setImage(image);
            event.setBookingCount(getBookingCount(event.getId()));
        }
        return event;
    }

    public Integer insertEventBooking(String bookingId, Integer userId, Integer eventId) {
        return template.update(SQL_INSERT_BOOKING, bookingId, userId, eventId);
    }

    public Integer getBookingCount(Integer eventId) {
        Integer count = 0;
        SqlRowSet rowSet = template.queryForRowSet(SQL_GET_BOOKING_COUNT_BY_EVENT_ID, eventId);
        while (rowSet.next()) {
            count = rowSet.getInt("count");
        }
        return count;
    }

    public List<EventBooking> getBookingsByUserId(Integer userId) throws Exception {
        List<EventBooking> bookingList = new ArrayList<>();
        SqlRowSet rowSet = template.queryForRowSet(SQL_GET_BOOKINGS_BY_USER_ID, userId);
        while (rowSet.next()) {
            EventBooking booking = EventBooking.createEvent(rowSet);
            bookingList.add(booking);
        }
        return bookingList;
    }

    
    public List<EventBooking> getBookingsByEventId(Integer eventId) throws Exception {
        List<EventBooking> bookingList = new ArrayList<>();
        SqlRowSet rowSet = template.queryForRowSet(SQL_GET_BOOKINGS_BY_EVENT_ID, eventId);
        while (rowSet.next()) {
            EventBooking booking = EventBooking.createEvent(rowSet);
            bookingList.add(booking);
        }
        logger.log(Level.INFO, bookingList.toString());
        return bookingList;
    }


    public Integer getBookingCountByUserAndEvent(Integer userId, Integer eventId) {
        Integer count = 0;
        SqlRowSet rowSet = template.queryForRowSet(SQL_GET_BOOKING_COUNT_BY_USER_ID_AND_EVENT_ID, userId, eventId);
        while (rowSet.next()) {
            count = rowSet.getInt("count");
        }
        return count;
    }

    public Optional<EventBooking> getBookingById(String bookingId) {
        EventBooking eventBooking = new EventBooking();
        SqlRowSet rowSet = template.queryForRowSet(SQL_GET_BOOKING_BY_BOOKING_ID, bookingId);
        while (rowSet.next()) {
            eventBooking = EventBooking.createEvent(rowSet);
            return Optional.of(eventBooking);
        }
        return Optional.empty();
    }

    public Integer deleteBookingsByEventId(Integer eventId) {
        Integer deleted = template.update(SQL_DELETE_BOOKINGS_BY_EVENT_ID, eventId);
        logger.log(Level.INFO, "Deleted " + deleted + " Bookings with Event Id: " + eventId);
        return deleted;
    }

    public Integer deleteBookingByBookingId(String bookingId) {
        Integer deleted = template.update(SQL_DELETE_BOOKINGS_BY_BOOKING_ID, bookingId);
        logger.log(Level.INFO, "Deleted Booking Id: " + bookingId);
        return deleted;
    }
}
