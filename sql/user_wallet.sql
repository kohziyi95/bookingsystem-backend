CREATE TABLE user_transactions (
    transaction_id varchar(50) not null,
    user_id bigint NOT NULL,
    incoming_funds float,
    incoming_description varchar(50),
    outgoing_funds float,
    outgoing_description varchar(50),
    total_credits float,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (transaction_id),
    foreign key (user_id) references users(id)
) 

-- CREATE TABLE user_wallet (
--     wallet_id int auto_increment,
--     user_id bigint NOT NULL,
--     credits float,
--     PRIMARY KEY (wallet_id),
--     FOREIGN KEY (user_id) REFERENCES users(id)
-- )