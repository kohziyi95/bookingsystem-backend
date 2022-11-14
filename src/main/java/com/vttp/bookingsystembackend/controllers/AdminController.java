package com.vttp.bookingsystembackend.controllers;

import java.io.IOException;
import java.util.ArrayList;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vttp.bookingsystembackend.models.EventDetails;
import com.vttp.bookingsystembackend.models.User;
import com.vttp.bookingsystembackend.services.EventService;
import com.vttp.bookingsystembackend.services.UserDetailsServiceImpl;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;

// import jakarta.json.Json;
// import jakarta.json.JsonObject;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/api/admin")
public class AdminController {
    private Logger logger = Logger.getLogger(AdminController.class.getName());

    @Autowired
    private EventService eventSvc;

    @Autowired
    private UserDetailsServiceImpl userSvc;

    @PostMapping(path = "/addEvent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addEvent(@RequestPart String eventDetails, @RequestPart MultipartFile image) {

        logger.log(Level.INFO,"Event Details Received >>>" + eventDetails);

        EventDetails event = EventDetails.createEvent(eventDetails);
        try {
            event.setImage(image.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        // System.out.println("Event Adding >>>" + event.toJson().toString());

        try {
            int updated = eventSvc.insertEvent(event);
            logger.log(Level.INFO,String.format("updated: %d\n", updated));
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

    @PostMapping(path = "/editEvent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> editEvent(@RequestPart String eventDetails) {

        // System.out.println("Event Details Received >>>" + eventDetails);

        EventDetails event = EventDetails.createEvent(eventDetails);
        event.setImage(EventDetails.getImageFromBase64(eventDetails));
        event.setBookingCount(eventSvc.getBookingCount(event.getId()));

       logger.log(Level.INFO,"Event Updating >>>" + event.getTitle());

        // if (image.isEmpty()){
        // try {
        // event.setImage(image.getBytes());
        // } catch (IOException e) {
        // e.printStackTrace();
        // return ResponseEntity.badRequest().body(e.getMessage());
        // }
        // }

        try {
            int updated = eventSvc.editEvent(event);
            logger.log(Level.INFO,String.format("updated: %d\n", updated));
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

    @PostMapping(path = "/editEventNewImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> editEventWithNewImage(@RequestPart String eventDetails,
            @RequestPart MultipartFile image) {

        System.out.println("Event Details Received With New Image >>>" + eventDetails);

        EventDetails event = EventDetails.createEvent(eventDetails);
        // System.out.println("Event Updating Before Image >>>" +
        // event.toJson().toString());

        try {
            event.setImage(image.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        try {
            System.out.println("Event Updating Before Image >>>" + event.toJson().toString());

            int updated = eventSvc.editEvent(event);
            logger.log(Level.INFO,String.format("updated: %d\n", updated));
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

    @DeleteMapping("/event/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEventById(@PathVariable Integer id) {
        logger.log(Level.INFO, "Deleting id >>>>> " + id);
        if (eventSvc.deleteEvent(id)) {
            return ResponseEntity.ok(id.toString());
        } else {
            return ResponseEntity.badRequest().body("Error. Failed to delete event.");
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<String> getUserById(@PathVariable Integer id) {
        User user = new User();
        System.out.println("Getting User Id: " + id);

        try {
            user = userSvc.getUserById(Long.valueOf(id));
            logger.log(Level.INFO, "User >>>> " + user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to get event.");
        }
        return ResponseEntity.ok(user.toJson().toString());
    }
}

 