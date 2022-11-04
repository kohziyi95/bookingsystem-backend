package com.vttp.bookingsystembackend.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vttp.bookingsystembackend.models.EventBooking;
import com.vttp.bookingsystembackend.models.EventDetails;
import com.vttp.bookingsystembackend.repositories.EventRepository;

@Service
public class EventService {
    private Logger logger = Logger.getLogger(EventService.class.getName());

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private DataSource datasource;

    @Autowired
    private EventRepository eventRepo;

    private static final String SQL_INSERT_EVENT = "insert into events(title, description, date, days, startDate, endDate, startTime, endTime, price, capacity, image) values (?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_GET_ALL_SINGLE_DAY_EVENT = "select * from events where days = 'single'";
    private static final String SQL_GET_ALL_MULTIPLE_DAY_EVENT = "select * from events where days = 'multiple'";
    public static final String SQL_GET_IMAGE_BY_ID = "select image from events where id = ?";
    private static final String SQL_GET_ALL_EVENTS = "select * from events";
    private static final String SQL_UPDATE_EVENT_BY_ID = "update events set title = ?, description = ?, date = ?, days = ?, startDate = ?, endDate = ?, startTime = ?, endTime = ?, price = ?, capacity = ?, image = ? where id = ?";

    public int insertEvent(EventDetails event) throws Exception {
        int updated = 0;
        if (event.getDays().equals("multiple")) {
            updated = template.update(SQL_INSERT_EVENT,
                    event.getTitle(),
                    event.getDescription(),
                    event.getStartDate(),
                    event.getDays(),
                    event.getStartDate(),
                    event.getEndDate(),
                    "N/A",
                    "N/A",
                    event.getPrice(),
                    event.getCapacity(),
                    event.getImage());
        } else {
            updated = template.update(SQL_INSERT_EVENT,
                    event.getTitle(),
                    event.getDescription(),
                    event.getDate(),
                    event.getDays(),
                    "N/A",
                    "N/A",
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getPrice(),
                    event.getCapacity(),
                    event.getImage());
        }
        return updated;
    }

    public int editEvent(EventDetails event) throws Exception {
        Connection con = datasource.getConnection();

        int updated = 0;
        PreparedStatement update = con.prepareStatement(SQL_UPDATE_EVENT_BY_ID);

        update.setString(1, event.getTitle());
        update.setString(2, event.getDescription());

        update.setString(4, event.getDays());

        update.setFloat(9, event.getPrice());
        update.setInt(10, event.getCapacity());
        update.setBytes(11, event.getImage());
        update.setInt(12, event.getId());

        if (event.getDays().equals("multiple")) {
            update.setString(3, event.getStartDate());
            update.setString(5, event.getStartDate());
            update.setString(6, event.getEndDate());
            update.setString(7, "N/A");
            update.setString(8, "N/A");
            // updated = template.update(SQL_UPDATE_EVENT_BY_ID,
            // event.getTitle(),
            // event.getDescription(),
            // event.getStartDate(),
            // event.getDays(),
            // event.getStartDate(),
            // event.getEndDate(),
            // "N/A",
            // "N/A",
            // event.getPrice(),
            // event.getCapacity(),
            // event.getImage(),
            // event.getId());
        } else {
            update.setString(3, event.getDate());
            update.setString(5, "N/A");
            update.setString(6, "N/A");
            update.setString(7, event.getStartTime());
            update.setString(8, event.getEndTime());
            // updated = template.update(SQL_UPDATE_EVENT_BY_ID,
            // event.getTitle(),
            // event.getDescription(),
            // event.getDate(),
            // event.getDays(),
            // "N/A",
            // "N/A",
            // event.getStartTime(),
            // event.getEndTime(),
            // event.getPrice(),
            // event.getCapacity(),
            // event.getImage(),
            // event.getId());
        }
        updated = update.executeUpdate();
        return updated;
    }

    public List<EventDetails> getAllSingleEvent() throws Exception {
        List<EventDetails> eventList = eventRepo.getEvents(SQL_GET_ALL_SINGLE_DAY_EVENT);
        return eventList;
    }

    // public Optional<byte[]> getImageById(Integer id) {
    // return template.query(SQL_GET_IMAGE_BY_ID,
    // (ResultSet rs) -> {
    // if (!rs.next())
    // return Optional.empty();
    // return Optional.of(rs.getBytes("image"));
    // },
    // id);
    // }

    public List<EventDetails> getAllMultipleEvent() throws Exception {
        List<EventDetails> eventList = eventRepo.getEvents(SQL_GET_ALL_MULTIPLE_DAY_EVENT);
        return eventList;
    }

    public List<EventDetails> getAllEvents() throws Exception {
        List<EventDetails> eventList = eventRepo.getEvents(SQL_GET_ALL_EVENTS);
        return eventList;
    }

    public List<EventBooking> getAllBookings(Integer userId) throws Exception {
        return eventRepo.getBookingsByUserId(userId);
    }

    @Transactional
    public boolean deleteEvent(Integer id) {
        int deleted = eventRepo.deleteBookingsByEventId(id);
        if (deleted >= 1) {
            return eventRepo.deleteEvents(id) >= 1;
        }
        return deleted >= 1;
    }

    public boolean deleteBooking(String bookingId){
        return eventRepo.deleteBookingByBookingId(bookingId) >= 1;
    }

    public EventDetails getEvent(Integer id) throws Exception {
        return eventRepo.getEventById(id);
    }

    public String addEventBooking(Integer userId, Integer eventId) throws Exception {
        Integer eventCapacity = eventRepo.getEventCapacity(eventId);
        Integer bookingCount = eventRepo.getBookingCount(eventId);
        Integer remainingCapacity = eventCapacity - bookingCount;
        if (remainingCapacity < 1) {
            throw new Exception("Event has reached maximum capacity.");
        } else if (bookingExists(userId, eventId)) {
            throw new Exception("Booking already exists.");
        } else {
            String bookingId = UUID.randomUUID().toString().substring(0, 8);
            logger.log(Level.INFO, "Booking ID: " + bookingId);
            if (eventRepo.insertEventBooking(bookingId, userId, eventId) > 0) {
                return bookingId;
            } else {
                throw new Exception("Failed to create booking.");
            }
        }
    }

    public boolean bookingExists(Integer userId, Integer eventId) {
        return eventRepo.getBookingCountByUserAndEvent(userId, eventId) > 0;
    }

    public Integer getBookingCount(Integer eventId) {
        return eventRepo.getBookingCount(eventId);
    }

}
