package com.vttp.bookingsystembackend.controllers;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vttp.bookingsystembackend.models.Transaction;
import com.vttp.bookingsystembackend.services.TransactionService;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private Logger logger = Logger.getLogger(TransactionController.class.getName());

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/credits/{userId}")
    public ResponseEntity<String> getCredits(@PathVariable Integer userId) {
        Float credits = transactionService.getCredits(userId);
        logger.log(Level.INFO, String.format("UserId >>> %s and Credits >>> %s", userId, credits));
        JsonObject obj = Json.createObjectBuilder().add("credits", credits).build();
        return ResponseEntity.ok(obj.toString());
    }

    @PostMapping("/topup")
    public ResponseEntity<String> postTopUpTransaction(@RequestBody String payload) {
        logger.log(Level.INFO, payload);
        JsonObject obj = Json.createReader(new StringReader(payload)).readObject();
        Integer userId = obj.getInt("userId");
        Float amount = Float.valueOf(obj.getString("amount"));
        logger.log(Level.INFO, String.format("Topping up $%f for user %d", amount, userId));
        Transaction t = new Transaction();
        if (amount >= 0) {
            t = Transaction.createIncomingTransaction(userId, amount, transactionService.getCredits(userId));
        }
        try {
            if (transactionService.addTransaction(t)) {
                JsonObject response = Json.createObjectBuilder()
                        .add("statusCode", 200)
                        .add("transactionId", t.getTransactionId())
                        .add("newCredit", t.getTotalCredits())
                        .build();
                return ResponseEntity.ok(response.toString());
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

    @PostMapping("/booking")
    public ResponseEntity<String> postBookingTransaction(@RequestBody String payload) {
        logger.log(Level.INFO, payload);
        JsonObject obj = Json.createReader(new StringReader(payload)).readObject();
        Integer userId = obj.getInt("userId");
        Float amount = Float.valueOf(obj.getString("amount"));
        String bookingId = obj.getString("bookingId");

        logger.log(Level.INFO, String.format("Topping up $%f for user %d", amount, userId));
        Transaction t = new Transaction();
        if (amount >= 0) {
            t = Transaction.createOutgoingTransaction(userId, amount, bookingId, transactionService.getCredits(userId));
        }
        try {
            if (transactionService.addTransaction(t)) {
                JsonObject response = Json.createObjectBuilder()
                        .add("statusCode", 200)
                        .add("transactionId", t.getTransactionId())
                        .add("newCredit", t.getTotalCredits())
                        .build();
                return ResponseEntity.ok(response.toString());
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

}
