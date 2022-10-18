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

@Service
public class EventService {
    @Autowired
    private JdbcTemplate template;

    private static final String SQL_INSERT_EVENT = "insert into events(title, description, date, days, startDate, endDate, startTime, endTime, price, capacity, image) values (?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_GET_ALL_SINGLE_DAY_EVENT = "select * from events where days = 'single'";
    private static final String SQL_GET_ALL_MULTIPLE_DAY_EVENT = "select * from events where days = 'multiple'";
    public static final String SQL_GET_IMAGE = "select image from events where id = ?";

    public int insertEvent(EventDetails event) throws Exception {
        int updated = 0;
        if (event.getDays().equals("multiple")) {
            updated = template.update(SQL_INSERT_EVENT,
                    event.getTitle(),
                    event.getDescription(),
                    "N/A",
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
        List<EventDetails> eventList = new ArrayList<>();
        SqlRowSet rowSet = template.queryForRowSet(SQL_GET_ALL_SINGLE_DAY_EVENT);
        while (rowSet.next()) {
            EventDetails event = EventDetails.createEvent(rowSet);
            byte[] image = getImage(event.getId()).get();
            // System.out.println("Getting image >>> " + image);
            event.setImage(image);
            eventList.add(event);
        }
        return eventList;
    }

    public Optional<byte[]> getImage(Integer id) {
        return template.query(SQL_GET_IMAGE,
                (ResultSet rs) -> {
                    if (!rs.next())
                        return Optional.empty();
                    return Optional.of(rs.getBytes("image"));
                },
                id);

    }

    public List<EventDetails> getAllMultipleEvent() {
        List<EventDetails> eventList = new ArrayList<>();
        SqlRowSet rowSet = template.queryForRowSet(SQL_GET_ALL_MULTIPLE_DAY_EVENT);
        while (rowSet.next()) {
            eventList.add(EventDetails.createEvent(rowSet));
        }
        return eventList;
    }

}
