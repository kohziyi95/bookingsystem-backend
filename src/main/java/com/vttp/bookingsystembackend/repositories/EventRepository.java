package com.vttp.bookingsystembackend.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.vttp.bookingsystembackend.models.EventDetails;

@Repository
public class EventRepository {
    
    @Autowired
    private JdbcTemplate template;

    public static final String SQL_GET_IMAGE_BY_ID = "select image from events where id = ?";
    public static final String SQL_DELETE_EVENT_BY_ID = "delete from events where id = ?";

    
    public List<EventDetails> getEvents(String SQL){
        List<EventDetails> eventList = new ArrayList<>();
        SqlRowSet rowSet = template.queryForRowSet(SQL);
        while (rowSet.next()) {
            EventDetails event = EventDetails.createEvent(rowSet);
            byte[] image = getImageById(event.getId()).get();
            // System.out.println("Getting image >>> " + image);
            event.setImage(image);
            eventList.add(event);
        }
        return eventList;
    }


    public Optional<byte[]> getImageById(Integer id) {
        return template.query(SQL_GET_IMAGE_BY_ID,
                (ResultSet rs) -> {
                    if (!rs.next())
                        return Optional.empty();
                    return Optional.of(rs.getBytes("image"));
                },
                id);
    }

    public Integer deleteEvents(Integer id){
        return template.update(SQL_DELETE_EVENT_BY_ID, id);
    }
}
