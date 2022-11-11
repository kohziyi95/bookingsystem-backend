package com.vttp.bookingsystembackend.models;

import java.util.UUID;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class Transaction {
    private String transactionId;
    private Integer userId;
    private Float incomingFunds;
    private Float outgoingFunds;
    private String incomingDescription;
    private String outgoingDescription;
    private Float totalCredits;

    public static Transaction createIncomingTransaction(Integer userId, Float incomingFunds, Float previousCredits) {

        Transaction t = new Transaction();
        t.setTransactionId(generateUUID());
        t.setUserId(userId);
        t.setIncomingFunds(incomingFunds);
        t.setIncomingDescription("Top Up");
        if (previousCredits == null) {
            t.setTotalCredits(t.calculateCredits(0f));
        } else {
            t.setTotalCredits(t.calculateCredits(previousCredits));
        }
        return t;
    }

    public static Transaction createOutgoingTransaction(Integer userId, Float outgoingFunds, String bookingId,
            Float previousCredits) {
        Transaction t = new Transaction();
        t.setTransactionId(generateUUID());
        t.setUserId(userId);
        t.setOutgoingFunds(outgoingFunds);
        t.setOutgoingDescription(String.format("Booking ID: %s", bookingId));
        if (previousCredits == null) {
            t.setTotalCredits(t.calculateCredits(0f));
        } else {
            t.setTotalCredits(t.calculateCredits(previousCredits));
        }
        return t;
    }

    private Float calculateCredits(Float previousCredits) {
        Float credits = previousCredits;
        try {
            credits += this.incomingFunds;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            credits -= this.outgoingFunds;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return credits;
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("transactionId", transactionId)
                .add("userId", userId)
                .add("totalCredits", totalCredits);
        if (this.incomingFunds != null) {
            builder.add("incomingFunds", incomingFunds)
                    .add("incomingDescription", incomingDescription);
        }
        if (this.outgoingFunds != null) {
            builder.add("outgoingFunds", outgoingFunds)
                    .add("outgoingDescription", outgoingDescription);
        }
        return builder.build();
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString().substring(0, 16);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Float getIncomingFunds() {
        return incomingFunds;
    }

    public void setIncomingFunds(Float incomingFunds) {
        this.incomingFunds = incomingFunds;
    }

    public Float getOutgoingFunds() {
        return outgoingFunds;
    }

    public void setOutgoingFunds(Float outgoingFunds) {
        this.outgoingFunds = outgoingFunds;
    }

    public String getIncomingDescription() {
        return incomingDescription;
    }

    public void setIncomingDescription(String incomingDescription) {
        this.incomingDescription = incomingDescription;
    }

    public String getOutgoingDescription() {
        return outgoingDescription;
    }

    public void setOutgoingDescription(String outgoingDescription) {
        this.outgoingDescription = outgoingDescription;
    }

    public Float getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(Float totalCredits) {
        this.totalCredits = totalCredits;
    }

}
