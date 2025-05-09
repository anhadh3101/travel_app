Use your terminal to make a database called "travel_planner". The name should should match exactly.

✅ 1. Log In to MySQL as Root

mysql -u root -p

✅ 2. Create a New Database

CREATE DATABASE travel_planner;

✅ 3. Create an Admin User

CREATE USER 'admin'@'localhost' IDENTIFIED BY 'mytravels';

✅ 4. Grant Privileges to the Admin User

GRANT ALL PRIVILEGES ON travel_planner.* TO 'admin'@'localhost';

✅ 5. Apply the Privileges

FLUSH PRIVILEGES;

✅ 6. Exit MySQL

EXIT;

✅ 7. Log In as Admin to Verify

mysql -u admin -p

USE travel_planner;

Create the table with the following format:

CREATE TABLE Customer (     
    customer_id INT AUTO_INCREMENT PRIMARY KEY,     
    name VARCHAR(100) NOT NULL,     
    email VARCHAR(100) NOT NULL,     
    phone VARCHAR(15) NOT NULL );

CREATE TABLE flight (
    flight_id INT AUTO_INCREMENT PRIMARY KEY,
    airline VARCHAR(100) NOT NULL,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE hotel (
    hotel_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    stars INT CHECK (stars BETWEEN 1 AND 5),
    available_rooms INT NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL
);

CREATE TABLE booking (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    flight_id INT,
    hotel_id INT,
    booking_date DATE NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flight(flight_id) ON DELETE SET NULL,
    FOREIGN KEY (hotel_id) REFERENCES hotel(hotel_id) ON DELETE SET NULL
);

run the following commands to start the app
1. mvn clean package  ->  (always run after making changes to a file to update the build)
2. java -jar target/sql-table-editor-1.0-SNAPSHOT-jar-with-dependencies.jar  ->   (to run the app)