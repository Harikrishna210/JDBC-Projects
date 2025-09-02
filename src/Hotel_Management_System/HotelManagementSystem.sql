CREATE DATABASE hotel_db;

USE hotel_db;

CREATE TABLE rooms (
    id INT PRIMARY KEY AUTO_INCREMENT,
    room_number INT UNIQUE,
    type VARCHAR(50),
    price DECIMAL(10,2),
    available BOOLEAN DEFAULT TRUE
);

CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100),
    room_id INT,
    days INT,
    total_price DECIMAL(10,2),
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

-- Sample rooms
INSERT INTO rooms (room_number, type, price) VALUES
(101, 'Single', 1500.00),
(102, 'Double', 2500.00),
(103, 'Suite', 5000.00);
