CREATE SCHEMA IF NOT EXISTS transportation;

-- Role table
CREATE TABLE IF NOT EXISTS transportation.role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- User table
CREATE TABLE IF NOT EXISTS transportation.user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES transportation.role(id)
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
INSERT INTO transportation.role (id, name) VALUES (1, 'CUSTOMER') ON DUPLICATE KEY UPDATE name = 'CUSTOMER';
INSERT INTO transportation.role (id, name) VALUES (2, 'DRIVER') ON DUPLICATE KEY UPDATE name = 'DRIVER';
INSERT INTO transportation.role (id, name) VALUES (3, 'MANAGER') ON DUPLICATE KEY UPDATE name = 'MANAGER';
INSERT INTO transportation.role (id, name) VALUES (4, 'SUPER_ADMIN') ON DUPLICATE KEY UPDATE name = 'SUPER_ADMIN';

-- Insert default order statuses
INSERT INTO transportation.order_status (id, status_name) VALUES (1, 'PENDING') ON DUPLICATE KEY UPDATE status_name = 'PENDING';
INSERT INTO transportation.order_status (id, status_name) VALUES (2, 'ASSIGNED') ON DUPLICATE KEY UPDATE status_name = 'ASSIGNED';
INSERT INTO transportation.order_status (id, status_name) VALUES (3, 'IN_TRANSIT') ON DUPLICATE KEY UPDATE status_name = 'IN_TRANSIT';
INSERT INTO transportation.order_status (id, status_name) VALUES (4, 'WAITING_UNLOADING') ON DUPLICATE KEY UPDATE status_name = 'WAITING_UNLOADING';
INSERT INTO transportation.order_status (id, status_name) VALUES (5, 'DELIVERED') ON DUPLICATE KEY UPDATE status_name = 'DELIVERED';
INSERT INTO transportation.order_status (id, status_name) VALUES (6, 'CANCELLED') ON DUPLICATE KEY UPDATE status_name = 'CANCELLED';
