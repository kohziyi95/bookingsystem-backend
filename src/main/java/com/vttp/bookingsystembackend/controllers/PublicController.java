package com.vttp.bookingsystembackend.controllers;

// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vttp.bookingsystembackend.models.EventDetails;
import com.vttp.bookingsystembackend.services.EventService;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class PublicController {

    @Autowired
    private EventService eventSvc;

    @GetMapping("/event/single")
    public ResponseEntity<String> getSingleEvent() {
        List<EventDetails> eventList = eventSvc.getAllSingleEvent();
        JsonArrayBuilder builder = Json.createArrayBuilder();
        eventList.forEach(e -> {
            builder.add(e.toJson());
            // System.out.println(e.getId());
        });
        // System.out.println(builder.build().toString());
        return ResponseEntity.ok(builder.build().toString());
    }

    @GetMapping("/event/all")
    public ResponseEntity<String> getAllEvents() {
        List<EventDetails> eventList = eventSvc.getAllEvents();
        JsonArrayBuilder builder = Json.createArrayBuilder();
        eventList.forEach(e -> {
            builder.add(e.toJson());
        });
        // System.out.println(builder.build().toString());
        return ResponseEntity.ok(builder.build().toString());
    }

    @GetMapping("/event/multiple")
    public ResponseEntity<String> getMultipleDayEvents() {
        List<EventDetails> eventList = eventSvc.getAllMultipleEvent();
        JsonArrayBuilder builder = Json.createArrayBuilder();
        eventList.forEach(e -> {
            builder.add(e.toJson());
        });
        // System.out.println(builder.build().toString());
        return ResponseEntity.ok(builder.build().toString());
    }

    @DeleteMapping("/event/{id}")
    public ResponseEntity<String> deleteEventById(@PathVariable Integer id){
        System.out.println("Deleting id >>>>> " + id);
        if (eventSvc.deleteEvent(id)){
            return ResponseEntity.ok(id.toString());
        } else {
            return ResponseEntity.badRequest().body("Error. Failed to delete event.");
        }

    }
}
