# ABase - Event & Ticket Management System

ABase is a comprehensive Java desktop application (Swing) designed to manage the complete lifecycle of events, ticketing, and user check-ins. The project is built upon a strict **Model-View-Controller (MVC)** architectural pattern, ensuring a clear separation of concerns between the graphical interface, business logic, and database transactions.

##  Technical Architecture & Features

This system was engineered to handle complex data relationships, enforce operational security, and maintain high cohesion with low coupling.

* **Strict MVC Architecture:** The application is rigidly divided into Views (pure Swing UIs), Controllers (business rules, validation, and mathematical processing), and DAOs/Models (database transactions). UIs have zero knowledge of the database layer.
* **Role-Based Access Control (RBAC):** Distinct workflows, controllers, and interfaces for Administrators (system management), Producers (event creation), and Clients (event discovery and check-in).
* **Geospatial Processing (Haversine Formula):** Integrates an external Geocoding API to convert string addresses into coordinates. The Client Controller calculates the exact linear distance between the user and available events to prioritize local discovery.
* **Secure Authentication:** Passwords are mathematically hashed using **BCrypt** before database insertion. Credentials are never stored or compared in plain text.
* **ACID Transactions:** Handles complex Many-to-Many (N:M) database relationships. Uses strict commit and rollback routines via JDBC to prevent data corruption during partial failures.
* **Environment Variable Protection:** Database host, port, and cloud credentials (Aiven) are securely loaded via a `.env` file, isolating sensitive infrastructure data from the source code.

##  User Roles & Capabilities

1.  **Administrator:** System gatekeeper. Approves Producer accounts and manages baseline domain tables (Event Locations, Ticket Vendors, Categories).
2.  **Producer:** Event creator. Manages their own event portfolio, applying multiple categories to events and hiding the relational database complexity through an intuitive UI.
3.  **Client:** The end-user. Searches for nearby events using text-to-coordinate tracking, registers ticket purchases, performs location-validated event check-ins, and submits post-event reviews.

##  Built With

* **Java 26 (Early Access):** Core application language.
* **Java Swing:** Native graphical user interface (GUI).
* **PostgreSQL:** Relational database hosted on the cloud (Aiven).
* **JDBC:** Database connectivity and SQL execution.
* **BCrypt:** Cryptographic hashing algorithm.

## 🛠️ How to Run Locally

### Prerequisites
1.  **Java JDK 26:** The application was compiled using Java 26. Running it on older versions (like Java 17 or 21) will result in an `UnsupportedClassVersionError`.
2.  **Internet Connection:** Required to connect to the remote PostgreSQL database and to resolve GPS coordinates.

### Setup Instructions
1. Clone this repository:
   ```bash
   git clone [https://github.com/viniciuspratadev/abase-gestao-eventos.git](https://github.com/viniciuspratadev/abase-gestao-eventos.git)
