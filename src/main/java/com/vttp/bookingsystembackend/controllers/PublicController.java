package com.vttp.bookingsystembackend.controllers;

import java.io.StringReader;
import java.util.ArrayList;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vttp.bookingsystembackend.models.EventBooking;
import com.vttp.bookingsystembackend.models.EventDetails;
import com.vttp.bookingsystembackend.models.Transaction;
import com.vttp.bookingsystembackend.services.EventService;
import com.vttp.bookingsystembackend.services.TransactionService;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class PublicController {
    private Logger logger = Logger.getLogger(PublicController.class.getName());

    @Autowired
    private EventService eventSvc;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/event/{id}")
    public ResponseEntity<String> getEventById(@PathVariable Integer id) {
        EventDetails event = new EventDetails();
        System.out.println("Getting Event Id: " + id);

        try {
            event = eventSvc.getEvent(Integer.valueOf(id));
            logger.log(Level.INFO, "Event >>>> " + event.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to get event.");
        }
        event.setBookingCount(eventSvc.getBookingCount(id));
        return ResponseEntity.ok(event.toJson().toString());
    }

    @GetMapping("/event/single")
    public ResponseEntity<String> getSingleEvent() {
        List<EventDetails> eventList = new ArrayList<>();
        try {
            eventList = eventSvc.getAllSingleEvent();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        JsonArrayBuilder builder = Json.createArrayBuilder();
        eventList.forEach(e -> {
            e.setBookingCount(eventSvc.getBookingCount(e.getId()));
            builder.add(e.toJson());
            // System.out.println(e.getId());
        });
        // System.out.println(builder.build().toString());
        return ResponseEntity.ok(builder.build().toString());
    }

    @GetMapping("/event/all")
    public ResponseEntity<String> getAllEvents() {
        List<EventDetails> eventList = new ArrayList<>();
        try {
            eventList = eventSvc.getAllEvents();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        JsonArrayBuilder builder = Json.createArrayBuilder();
        eventList.forEach(e -> {
            e.setBookingCount(eventSvc.getBookingCount(e.getId()));
            builder.add(e.toJson());
        });
        // System.out.println(builder.build().toString());
        return ResponseEntity.ok(builder.build().toString());
    }

    @GetMapping("/event/multiple")
    public ResponseEntity<String> getMultipleDayEvents() {
        List<EventDetails> eventList = new ArrayList<>();
        try {
            eventList = eventSvc.getAllMultipleEvent();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        JsonArrayBuilder builder = Json.createArrayBuilder();
        eventList.forEach(e -> {
            e.setBookingCount(eventSvc.getBookingCount(e.getId()));
            builder.add(e.toJson());
        });
        // System.out.println(builder.build().toString());
        return ResponseEntity.ok(builder.build().toString());
    }

    @PostMapping("/event/{eventId}/book")
    public ResponseEntity<String> bookEvent(@PathVariable Integer eventId, @RequestBody String payload) {
        JsonObject data = Json.createReader(new StringReader(payload)).readObject();
        Integer userId = data.getInt("userId");
        String bookingId = UUID.randomUUID().toString().substring(0, 8);
        Float amount = -999999999f;
        try {
            amount = eventSvc.getEvent(eventId).getPrice();
            Transaction t = new Transaction();
            if (amount >= 0) {
                t = Transaction.createOutgoingTransaction(userId, amount, bookingId,
                        transactionService.getCredits(userId));
                try {
                    if (transactionService.addTransaction(t)) {
                        logger.log(Level.INFO, "Creating booking for eventId: " + eventId + " and userId: " + userId);
                        logger.log(Level.INFO, String.format("Deducting $%f from user %d", amount, userId));

                        try {
                            bookingId = eventSvc.addEventBooking(userId, eventId, bookingId);
                            JsonObject response = Json.createObjectBuilder()
                                    .add("statusCode", 200)
                                    .add("transactionId", t.getTransactionId())
                                    .add("newCredit", t.getTotalCredits())
                                    .add("bookingId", bookingId)
                                    .build();
                            return ResponseEntity.ok(response.toString());
                        } catch (Exception e) {
                            // e.printStackTrace();
                            logger.log(Level.WARNING, e.getMessage());
                            logger.log(Level.WARNING,
                                    "Failed to create booking for eventId: " + eventId + " and userId: " + userId);
                            JsonObject response = Json.createObjectBuilder()
                                    .add("statusCode", 400)
                                    .add("message",
                                            "Failed to create booking for eventId: " + eventId + " and userId: "
                                                    + userId)
                                    .build();
                            return ResponseEntity.badRequest().body(response.toString());
                            // return ResponseEntity.badRequest().body(e.getMessage());
                        }

                    } else {
                        JsonObject response = Json.createObjectBuilder()
                                .add("statusCode", 400)
                                .add("message", "Transaction Failed.")
                                .build();
                        return ResponseEntity.badRequest().body(response.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JsonObject response = Json.createObjectBuilder()
                            .add("statusCode", 400)
                            .add("message", e.getMessage())
                            .build();
                    return ResponseEntity.badRequest().body(response.toString());
                }
            }
        } catch (Exception e1) {
            logger.log(Level.WARNING, e1.getMessage());
            e1.printStackTrace();
            JsonObject response = Json.createObjectBuilder()
                    .add("statusCode", 400)
                    .add("message", "Unable to retrieve event details.")
                    .build();
            return ResponseEntity.badRequest().body(response.toString());
        }
        JsonObject response = Json.createObjectBuilder()
                .add("statusCode", 400)
                .add("message", "Unknown error.")
                .build();
        return ResponseEntity.badRequest().body(response.toString());
    }

    @GetMapping("/event/{eventId}/count")
    public ResponseEntity<Integer> getBookingCount(@PathVariable Integer eventId) {
        Integer bookingCount = eventSvc.getBookingCount(eventId);
        return ResponseEntity.ok(bookingCount);
    }

    @GetMapping("/event/bookings/user/{userId}")
    public ResponseEntity<String> getBookingsByUser(@PathVariable Integer userId) {
        try {
            List<EventBooking> bookingList = eventSvc.getAllBookings(userId);
            JsonArrayBuilder builder = Json.createArrayBuilder();
            bookingList.forEach(e -> {
                builder.add(e.toJson());
            });
            return ResponseEntity.ok(builder.build().toString());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }

    @DeleteMapping("/event/bookings/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable String bookingId) {
        logger.log(Level.INFO, "Deleting Booking Id >>>>> " + bookingId);

        EventBooking bookingDetails;
        EventDetails e;
        try {
            bookingDetails = eventSvc.getBookingByBookingId(bookingId);
            try {
                e = eventSvc.getEvent(bookingDetails.getEventId());
                Float previousCredits = transactionService.getCredits(bookingDetails.getUserId());
                Transaction t = Transaction.createIncomingTransaction(bookingDetails.getUserId(), e.getPrice(),
                        previousCredits);
                t.setIncomingDescription(String.format("Refund for cancellation of booking Id: %s", bookingId));
                if (transactionService.addTransaction(t)) {
                    if (eventSvc.deleteBooking(bookingId)) {

                        JsonObject response = Json.createObjectBuilder()
                                .add("statusCode", 200)
                                .add("transactionId", t.getTransactionId())
                                .add("newCredit", t.getTotalCredits())
                                .build();
                        return ResponseEntity.ok(response.toString());
                    } else {
                        JsonObject response = Json.createObjectBuilder()
                                .add("statusCode", 400)
                                .add("message", "Error. Failed to delete event.")
                                .build();
                        return ResponseEntity.badRequest().body(response.toString());
                    }
                } else {
                    JsonObject response = Json.createObjectBuilder()
                            .add("statusCode", 400)
                            .add("message", "Transaction Failed. Unable to update your balance.")
                            .build();
                    return ResponseEntity.badRequest().body(response.toString());
                }

            } catch (Exception e1) {
                e1.printStackTrace();
                JsonObject response = Json.createObjectBuilder()
                        .add("statusCode", 400)
                        .add("message", "Unable to retrieve booking details.")
                        .build();
                return ResponseEntity.badRequest().body(response.toString());

            }
        } catch (Exception e1) {
            e1.printStackTrace();
            JsonObject response = Json.createObjectBuilder()
                    .add("statusCode", 400)
                    .add("message", "Unable to retrieve event details.")
                    .build();
            return ResponseEntity.badRequest().body(response.toString());
        }

    }
}
