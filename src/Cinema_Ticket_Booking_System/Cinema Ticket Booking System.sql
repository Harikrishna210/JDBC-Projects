CREATE DATABASE cinemadb;

USE cinemadb;

CREATE TABLE movies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    show_time VARCHAR(50),
    price DECIMAL(10,2)
);

CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(50),
    movie_id INT,
    seats INT,
    total_price DECIMAL(10,2),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

-- Sample movie data
INSERT INTO movies (title, show_time, price) VALUES
('Avengers: Endgame', '7:00 PM', 250.00),
('Inception', '9:30 PM', 200.00),
('Interstellar', '6:00 PM', 220.00);
