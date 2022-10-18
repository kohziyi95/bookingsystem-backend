package com.vttp.bookingsystembackend.controllers;

import java.io.IOException;
import java.util.ArrayList;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vttp.bookingsystembackend.models.EventDetails;
import com.vttp.bookingsystembackend.services.EventService;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;

// import jakarta.json.Json;
// import jakarta.json.JsonObject;

@RestController
@RequestMapping(path = "/api/admin")
public class AdminController {

    @Autowired
    private EventService eventSvc;

    @PostMapping(path = "/addEvent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addEvent(@RequestPart String eventDetails, @RequestPart MultipartFile image) {

        System.out.println("Event Details Received >>>" + eventDetails);

        EventDetails event = EventDetails.createEvent(eventDetails);
        try {
            event.setImage(image.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        try {
            int updated = eventSvc.insertEvent(event);
            System.out.printf("updated: %d\n", updated);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

        // JsonObject data = Json.createObjectBuilder()
        // .add("content-type", image.getContentType())
        // .add("name", image.getName())
        // .add("original_name", image.getOriginalFilename())
        // .add("size", image.getSize())
        // .add("form_title", event.getTitle())
        // .build();

        return ResponseEntity.ok(event.toJson().toString());
    }

    

}
