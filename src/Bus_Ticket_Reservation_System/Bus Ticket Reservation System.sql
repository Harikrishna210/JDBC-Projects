CREATE DATABASE busdb;

USE busdb;

CREATE TABLE buses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bus_name VARCHAR(50),
    source VARCHAR(50),
    destination VARCHAR(50),
    seats_available INT
);

CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bus_id INT,
    passenger_name VARCHAR(50),
    seats_booked INT,
    FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE CASCADE
);

-- Add some buses
INSERT INTO buses (bus_name, source, destination, seats_available)
VALUES 
('Express 101', 'CityA', 'CityB', 40),
('Express 202', 'CityB', 'CityC', 35);
