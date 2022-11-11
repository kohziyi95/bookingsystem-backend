package com.vttp.bookingsystembackend.repositories;

import java.sql.ResultSet;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vttp.bookingsystembackend.models.Transaction;

@Repository
public class TransactionRepository {
    private Logger logger = Logger.getLogger(TransactionRepository.class.getName());

    @Autowired
    private JdbcTemplate template;

    public static final String SQL_GET_LAST_CREDIT_BY_USER_ID = "select total_credits from user_transactions where user_id = ? order by date_created desc limit 1";
    public static final String SQL_INSERT_TRANSACTION = "insert into user_transactions(transaction_id, user_id, incoming_funds, incoming_description, outgoing_funds, outgoing_description, total_credits) values(?, ?, ?, ?, ?, ?, ?)";

    public Integer insertTransaction(Transaction t) {
        return template.update(SQL_INSERT_TRANSACTION,
                t.getTransactionId(),
                t.getUserId(),
                t.getIncomingFunds(),
                t.getIncomingDescription(),
                t.getOutgoingFunds(),
                t.getOutgoingDescription(),
                t.getTotalCredits());
    }

    public Optional<Float> getCredits(Integer userId) {
        return template.query(SQL_GET_LAST_CREDIT_BY_USER_ID, (ResultSet rs) -> {
            if (!rs.next())
                return Optional.empty();
            return Optional.of(rs.getFloat("total_credits"));
        }, userId);
    }
}
