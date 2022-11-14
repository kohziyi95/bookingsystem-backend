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
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vttp.bookingsystembackend.models.EventBooking;
import com.vttp.bookingsystembackend.models.EventDetails;
import com.vttp.bookingsystembackend.repositories.EventRedisRepository;
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

    @Autowired
    private EventRedisRepository eventRedisRepo;

    @Autowired
    private TransactionService transactionService;

    private static final String SQL_INSERT_EVENT = "insert into events(title, description, date, days, startDate, endDate, startTime, endTime, price, capacity, image) values (?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_GET_ALL_SINGLE_DAY_EVENT = "select * from events where days = 'single'";
    private static final String SQL_GET_ALL_MULTIPLE_DAY_EVENT = "select * from events where days = 'multiple'";
    public static final String SQL_GET_IMAGE_BY_ID = "select image from events where id = ?";
    private static final String SQL_GET_ALL_EVENTS = "select * from events";
    private static final String SQL_UPDATE_EVENT_BY_ID = "update events set title = ?, description = ?, date = ?, days = ?, startDate = ?, endDate = ?, startTime = ?, endTime = ?, price = ?, capacity = ?, image = ? where id = ?";

    public EventDetails getEvent(Integer id) throws Exception {
        return eventRepo.getEventById(id);
    }

    public List<EventDetails> getAllEvents() throws Exception {
        List<EventDetails> eventList = new ArrayList<>();
        if (eventRedisRepo.count() >= 1) {
            eventList = (List<EventDetails>) eventRedisRepo.findAll();
            logger.log(Level.INFO,
                    String.format("Retrieved %d events from REDIS", eventList.size()));
        } else {
            eventList = eventRepo.getEvents(SQL_GET_ALL_EVENTS);
            ;
            logger.log(Level.INFO,
                    String.format("Retrieved %d events from MYSQL", eventList.size()));
            eventList.forEach(e -> eventRedisRepo.save(e));
        }
        return eventList;
    }

    public List<EventDetails> getAllSingleEvent() throws Exception {
        List<EventDetails> eventList = new ArrayList<>();
        if (eventRedisRepo.count() >= 1) {
            eventList = (List<EventDetails>) eventRedisRepo.findAll();
            List<EventDetails> filteredList = eventList.stream().filter(e -> e.getDays().equals("single"))
                    .collect(Collectors.toList());
            eventList = filteredList;
            logger.log(Level.INFO,
                    String.format("Retrieved %d single day events from REDIS", filteredList.size()));
        } else {
            eventList = eventRepo.getEvents(SQL_GET_ALL_SINGLE_DAY_EVENT);
            logger.log(Level.INFO,
                    String.format("Retrieved %d single day events from MYSQL", eventList.size()));
            eventList.forEach(e -> eventRedisRepo.save(e));

        }
        return eventList;
    }

    public List<EventDetails> getAllMultipleEvent() throws Exception {
        List<EventDetails> eventList = new ArrayList<>();
        if (eventRedisRepo.count() >= 1) {
            eventList = (List<EventDetails>) eventRedisRepo.findAll();
            List<EventDetails> filteredList = eventList.stream().filter(e -> e.getDays().equals("multiple"))
                    .collect(Collectors.toList());
            eventList = filteredList;
            logger.log(Level.INFO,
                    String.format("Retrieved %d multiple day events from REDIS", filteredList.size()));
        } else {
            eventList = eventRepo.getEvents(SQL_GET_ALL_MULTIPLE_DAY_EVENT);
            logger.log(Level.INFO,
                    String.format("Retrieved %d multiple day events from MYSQL", eventList.size()));
            eventList.forEach(e -> eventRedisRepo.save(e));

        }
        return eventList;
    }

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
        if (updated >= 1) {
            logger.log(Level.INFO, "Event added.");
            EventDetails savedEvent = eventRepo.getLatestEventByTitle(event.getTitle());
            eventRedisRepo.save(savedEvent);
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
        } else {
            update.setString(3, event.getDate());
            update.setString(5, "N/A");
            update.setString(6, "N/A");
            update.setString(7, event.getStartTime());
            update.setString(8, event.getEndTime());
        }
        updated = update.executeUpdate();
        if (updated >= 1) {
            logger.log(Level.INFO, "Event Edited.");
            eventRedisRepo.deleteById(event.getId());
            eventRedisRepo.save(event);
        }
        return updated;
    }

    public boolean deleteEvent(Integer id) {
        int deleted = eventRepo.deleteBookingsByEventId(id);
        logger.log(Level.INFO, String.format("%d bookings deleted for Event ID: %d", deleted, id));
        if (eventRepo.deleteEvents(id) >= 1) {
            eventRedisRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Event Bookings Methods

    public boolean bookingExists(Integer userId, Integer eventId) {
        return eventRepo.getBookingCountByUserAndEvent(userId, eventId) > 0;
    }

    public Integer getBookingCount(Integer eventId) {
        return eventRepo.getBookingCount(eventId);
    }

    public List<EventBooking> getAllBookings(Integer userId) throws Exception {
        return eventRepo.getBookingsByUserId(userId);
    }

    public List<EventBooking> getAllBookingsByEvent(Integer eventId) throws Exception {
        return eventRepo.getBookingsByEventId(eventId);
    }

    public String addEventBooking(Integer userId, Integer eventId, String bookingId) throws Exception {
        Integer eventCapacity = eventRepo.getEventCapacity(eventId);
        Integer bookingCount = eventRepo.getBookingCount(eventId);
        Integer remainingCapacity = eventCapacity - bookingCount;
        if (remainingCapacity < 1) {
            throw new Exception("Event has reached maximum capacity.");
        } else if (bookingExists(userId, eventId)) {
            throw new Exception("Booking already exists.");
        } else {
            logger.log(Level.INFO, "Booking ID: " + bookingId);
            if (eventRepo.insertEventBooking(bookingId, userId, eventId) > 0) {
                return bookingId;
            } else {
                throw new Exception("Failed to create booking.");
            }
        }
    }

    public boolean deleteBooking(String bookingId) {
        return eventRepo.deleteBookingByBookingId(bookingId) >= 1;
    }

    public EventBooking getBookingByBookingId(String bookingId) throws Exception {
        Optional<EventBooking> opt = eventRepo.getBookingById(bookingId);
        if (opt.isEmpty()) {
            throw new Exception(String.format("No booking found with booking id %s", bookingId));
        } else {
            return opt.get();
        }
    }

}
