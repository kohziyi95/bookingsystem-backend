-- JPA Will Initialise the USER, ROLES, and USER_ROLES Tables

-- Run the following after starting the application
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');