DROP TABLE IF EXISTS events;

CREATE TABLE events (
id int auto_increment primary key,
title varchar(50) not null,
description varchar(400),
date varchar(50),
days varchar(10),
startDate varchar(50),
endDate varchar(50),
startTime varchar(10),
endTime varchar(10),
image mediumblob,
price float,
capacity int
);


