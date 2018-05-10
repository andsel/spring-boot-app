CREATE TABLE IF NOT EXISTS users (
 id bigint(20) NOT NULL AUTO_INCREMENT,
 username varchar(200) NOT NULL,
 password varchar(200) NOT NULL,
 role varchar(32) NOT NULL,
 enabled boolean DEFAULT false,
 locked boolean DEFAULT false,
 PRIMARY KEY (id)
);
