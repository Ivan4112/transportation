CREATE SCHEMA IF NOT EXISTS transportation;

-- Drop existing tables if they exist to avoid conflicts
DROP TABLE IF EXISTS transportation.order_location;
DROP TABLE IF EXISTS transportation.cargo;
DROP TABLE IF EXISTS transportation.route;
DROP TABLE IF EXISTS transportation.orders;
DROP TABLE IF EXISTS transportation.vehicle;
DROP TABLE IF EXISTS transportation.user;
DROP TABLE IF EXISTS transportation.order_status;
DROP TABLE IF EXISTS transportation.role;

-- Role table with email field and renamed name field to role_name
CREATE TABLE IF NOT EXISTS transportation.role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE
);

-- User table with modified fields
CREATE TABLE IF NOT EXISTS transportation.user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    firstname VARCHAR(100),
    lastname VARCHAR(100),
    role_name VARCHAR(50) NOT NULL
);

-- Vehicle table
CREATE TABLE IF NOT EXISTS transportation.vehicle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    driver_id INT NOT NULL,
    license_plate VARCHAR(15) NOT NULL,
    photo_url TEXT,
    capacity DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (driver_id) REFERENCES transportation.user(id)
);

-- Order status table
CREATE TABLE IF NOT EXISTS transportation.order_status (
    id INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL
);

-- Order table
CREATE TABLE IF NOT EXISTS transportation.orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    driver_id INT,
    vehicle_id INT,
    status_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES transportation.user(id),
    FOREIGN KEY (driver_id) REFERENCES transportation.user(id),
    FOREIGN KEY (vehicle_id) REFERENCES transportation.vehicle(id),
    FOREIGN KEY (status_id) REFERENCES transportation.order_status(id)
);

-- Route table
CREATE TABLE IF NOT EXISTS transportation.route (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    start_location VARCHAR(255) NOT NULL,
    end_location VARCHAR(255) NOT NULL,
    estimated_time TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES transportation.orders(id)
);

-- Cargo table
CREATE TABLE IF NOT EXISTS transportation.cargo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    type VARCHAR(100) NOT NULL,
    weight DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES transportation.orders(id)
);

-- Order location table for real-time tracking
CREATE TABLE IF NOT EXISTS transportation.order_location (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES transportation.orders(id)
);

-- Insert default roles
-- INSERT INTO transportation.role (role_name, email) VALUES ('DRIVER', 'driver@transport.com');
-- INSERT INTO transportation.role (role_name, email) VALUES ('MANAGER', 'manager@transport.com');
-- INSERT INTO transportation.role (role_name, email) VALUES ('SUPER_ADMIN', 'admin@transport.com');

-- Insert default order statuses
-- INSERT INTO transportation.order_status (status_name) VALUES ('PENDING');
-- INSERT INTO transportation.order_status (status_name) VALUES ('ASSIGNED');
-- INSERT INTO transportation.order_status (status_name) VALUES ('IN_TRANSIT');
-- INSERT INTO transportation.order_status (status_name) VALUES ('WAITING_UNLOADING');
-- INSERT INTO transportation.order_status (status_name) VALUES ('DELIVERED');
-- INSERT INTO transportation.order_status (status_name) VALUES ('CANCELLED');
