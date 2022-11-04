package com.vttp.bookingsystembackend.models;

import java.io.StringReader;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.multipart.MultipartFile;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;

public class EventDetails {
    private Integer id;
    private String title;
    private String description;
    private String date;
    private String days;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private Float price;
    private Integer capacity;
    private byte[] image;
    private Integer bookingCount;



    public static EventDetails createEvent(String json) {
        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject data = reader.readObject();
        String dayNumber = data.getString("days");
        if (dayNumber.equals("multiple")) {
            return createMultipleDayEvent(data);
        } else {
            return createSingleDayEvent(data);
        }
    }

    public static byte[] getImageFromBase64(String json) {
        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject data = reader.readObject();
        String base64ImageString = data.getString("image");
        return Base64.decodeBase64(base64ImageString);
    }

    public static EventDetails createEvent(SqlRowSet rowSet) {
        EventDetails e = new EventDetails();
        e.setId(rowSet.getInt("id"));
        e.setTitle(rowSet.getString("title"));
        e.setDescription(rowSet.getString("description"));
        e.setDays(rowSet.getString("days"));
        String dayNumber = e.getDays();
        if (dayNumber.equals("multiple")) {
            e.setStartDate(rowSet.getString("startDate"));
            e.setEndDate(rowSet.getString("endDate"));
        } else {
            e.setDate(rowSet.getString("date"));
            e.setStartTime(rowSet.getString("startTime"));
            e.setEndTime(rowSet.getString("endTime"));
        }
        e.setPrice(rowSet.getFloat("price"));
        e.setCapacity(rowSet.getInt("capacity"));
        // e.setImage(rowSet.get);
        return e;
    }

    private static EventDetails createSingleDayEvent(JsonObject data) {
        EventDetails e = new EventDetails();
        try {
            e.setId(data.getInt("id"));
        } catch (Exception ex) {
            // TODO: handle exception
            ex.getMessage();
        }
        e.setTitle(data.getString("title"));
        e.setDescription(data.getString("description"));
        e.setDays(data.getString("days"));
        e.setDate(data.getString("date"));
        e.setStartTime(data.getString("startTime"));
        e.setEndTime(data.getString("endTime"));
        e.setPrice(Float.valueOf(data.get("price").toString()));
        e.setCapacity(Integer.valueOf(data.get("capacity").toString()));
        return e;
    }

    private static EventDetails createMultipleDayEvent(JsonObject data) {
        EventDetails e = new EventDetails();
        try {
            e.setId(data.getInt("id"));
        } catch (Exception ex) {
            ex.getMessage();
        }
        e.setTitle(data.getString("title"));
        e.setDescription(data.getString("description"));
        e.setDays(data.getString("days"));
        e.setStartDate(data.getString("startDate"));
        e.setEndDate(data.getString("endDate"));
        e.setPrice(Float.valueOf(data.get("price").toString()));
        e.setCapacity(Integer.valueOf(data.get("capacity").toString()));
        return e;
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("title", title)
                .add("description", description)
                .add("days", days)
                .add("price", price)
                .add("capacity", capacity)
                .add("image", Base64.encodeBase64String(image))
                .add("bookingCount", bookingCount);
        if (this.id != null) {
            builder.add("id", id);
        }
        if (this.days.equals("multiple")) {
            builder.add("startDate", startDate)
                    .add("endDate", endDate);
        } else {
            builder.add("date", date)
                    .add("startTime", startTime)
                    .add("endTime", endTime);
        }
        return builder.build();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Integer getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(Integer bookingCount) {
        this.bookingCount = bookingCount;
    }

}
