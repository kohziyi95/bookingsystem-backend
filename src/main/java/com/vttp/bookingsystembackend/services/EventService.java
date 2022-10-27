package com.vttp.bookingsystembackend.services;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.vttp.bookingsystembackend.models.EventDetails;
import com.vttp.bookingsystembackend.repositories.EventRepository;

@Service
public class EventService {
    @Autowired
    private JdbcTemplate template;

    @Autowired
    private EventRepository eventRepo;

    private static final String SQL_INSERT_EVENT = "insert into events(title, description, date, days, startDate, endDate, startTime, endTime, price, capacity, image) values (?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_GET_ALL_SINGLE_DAY_EVENT = "select * from events where days = 'single'";
    private static final String SQL_GET_ALL_MULTIPLE_DAY_EVENT = "select * from events where days = 'multiple'";
    public static final String SQL_GET_IMAGE_BY_ID = "select image from events where id = ?";
    private static final String SQL_GET_ALL_EVENTS = "select * from events";

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

    public List<EventDetails> getAllSingleEvent() {
        List<EventDetails> eventList = eventRepo.getEvents(SQL_GET_ALL_SINGLE_DAY_EVENT);
        return eventList;
    }

    // public Optional<byte[]> getImageById(Integer id) {
    //     return template.query(SQL_GET_IMAGE_BY_ID,
    //             (ResultSet rs) -> {
    //                 if (!rs.next())
    //                     return Optional.empty();
    //                 return Optional.of(rs.getBytes("image"));
    //             },
    //             id);
    // }

    public List<EventDetails> getAllMultipleEvent() {
        List<EventDetails> eventList = eventRepo.getEvents(SQL_GET_ALL_MULTIPLE_DAY_EVENT);
        return eventList;
    }

    public List<EventDetails> getAllEvents() {
        List<EventDetails> eventList = eventRepo.getEvents(SQL_GET_ALL_EVENTS);

        return eventList;
    }

    public boolean deleteEvent(Integer id){
        int deleted = eventRepo.deleteEvents(id);
        return deleted >= 1;
    }

}
