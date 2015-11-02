CREATE DATABASE IF NOT EXISTS opinion;

USE opinion;

CREATE TABLE grid (
	id INT AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    schedule TIME,
    description TEXT,
    PRIMARY KEY (id)
)ENGINE=InnoDB;

CREATE TABLE presenter (
	id INT AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    PRIMARY KEY (id)
)ENGINE=InnoDB;

CREATE TABLE grid_presenter (
	id INT AUTO_INCREMENT,
    presenter_id INT NOT NULL,
    grid_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (presenter_id) REFERENCES presenter(id),
    FOREIGN KEY (grid_id) REFERENCES grid(id)
)ENGINE=InnoDB;

CREATE TABLE user (
	id INT AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    address VARCHAR(200),
    phone_number VARCHAR(11),
    PRIMARY KEY (id)
)ENGINE=InnoDB;

CREATE TABLE comment (
	id INT AUTO_INCREMENT,
    user_id INT NOT NULL,
    grid_id INT NOT NULL,
    description TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (grid_id) REFERENCES grid(id)
)ENGINE=InnoDB;

CREATE TABLE vote (
	id INT AUTO_INCREMENT,
    user_id INT NOT NULL,
    grid_id INT NOT NULL,
    scale INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (grid_id) REFERENCES grid(id)
)ENGINE=InnoDB;