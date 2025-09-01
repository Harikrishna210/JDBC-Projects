CREATE DATABASE payroll_db;
USE payroll_db;

CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    salary DECIMAL(10,2) NOT NULL
);

CREATE TABLE payroll (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT,
    pay_date DATE,
    amount DECIMAL(10,2),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);
