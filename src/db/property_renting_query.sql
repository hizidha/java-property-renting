CREATE DATABASE property_renting_db;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'INACTIVE')) NOT NULL
);

CREATE TABLE properties (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'INACTIVE')) NOT NULL
);

CREATE TABLE booking_record (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    properties_id INT REFERENCES properties(id) ON DELETE CASCADE,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert dummy data into the user table
INSERT INTO users (name, email, phone, status) VALUES
('John Doe', 'johndoe@example.com', '123-456-7890', 'ACTIVE'),
('Jane Smith', 'janesmith@example.com', '234-567-8901', 'INACTIVE'),
('Alice Johnson', 'alicej@example.com', '345-678-9012', 'ACTIVE'),
('Bob Williams', 'bobw@example.com', '456-789-0123', 'ACTIVE'),
('Charlie Brown', 'charlieb@example.com', '567-890-1234', 'INACTIVE'),
('Dave Adams', 'davea@example.com', '678-901-2345', 'ACTIVE'),
('Emma Watson', 'emmaw@example.com', '789-012-3456', 'ACTIVE'),
('Frank Miller', 'frankm@example.com', '890-123-4567', 'INACTIVE'),
('Grace Lee', 'gracel@example.com', '901-234-5678', 'ACTIVE'),
('Henry White', 'henryw@example.com', '012-345-6789', 'ACTIVE');

-- Insert dummy data into the properties table
INSERT INTO properties (name, location, description, status) VALUES
('Central Park Apartment', 'New York, NY', 'A cozy apartment near Central Park.', 'ACTIVE'),
('Sunny Beach House', 'Miami, FL', 'A beautiful house by the beach.', 'ACTIVE'),
('Mountain Cabin', 'Denver, CO', 'A quiet cabin in the mountains.', 'INACTIVE'),
('Downtown Loft', 'San Francisco, CA', 'A modern loft in downtown.', 'ACTIVE'),
('Country Cottage', 'Nashville, TN', 'A charming cottage in the countryside.', 'ACTIVE'),
('Lakeview Villa', 'Orlando, FL', 'A luxury villa with a lake view.', 'INACTIVE'),
('City Center Studio', 'Chicago, IL', 'A small studio in the city center.', 'ACTIVE'),
('Oceanfront Condo', 'Los Angeles, CA', 'A condo with oceanfront views.', 'ACTIVE'),
('Suburban Home', 'Austin, TX', 'A spacious home in the suburbs.', 'INACTIVE'),
('Penthouse Suite', 'Las Vegas, NV', 'A luxurious penthouse suite.', 'INACTIVE');

-- Insert dummy data into the booking_record table
INSERT INTO booking_record (user_id, properties_id, check_in_date, check_out_date) VALUES
(1, 1, '2024-09-01', '2024-09-05'),
(2, 2, '2024-09-10', '2024-09-15'),
(3, 3, '2024-09-05', '2024-09-12'),
(4, 4, '2024-09-20', '2024-09-25'),
(5, 5, '2024-09-18', '2024-09-22'),
(6, 6, '2024-09-22', '2024-09-30'),
(7, 7, '2024-09-08', '2024-09-12'),
(8, 8, '2024-09-03', '2024-09-08'),
(9, 9, '2024-09-15', '2024-09-20'),
(10, 10, '2024-07-25', '2024-07-30');