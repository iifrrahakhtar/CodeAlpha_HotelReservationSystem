# 🏨 The Luminous Grand Hotel Terminal

A premium, desktop-based enterprise administration application designed to streamline hotel operations, room occupancy tracking, and guest booking workflows. Built with a heavy focus on custom UI/UX design and structured relational database safety.

---

## 🛠️ Tech Stack & Architecture

* **Frontend GUI:** Java Swing & AWT (`Graphics2D`, `CardLayout`, `GridBagLayout`)
* **Backend Database:** SQLite (ACID-compliant local embedded database)
* **Data Connectivity:** JDBC (Java Database Connectivity)
* **Time Management:** Java Calendar API & `SimpleDateFormat`

---

## 🚀 Key Features

* **Dynamic Room Inventory Engine:** Automates room categorization into distinct tiers (Standard, Deluxe, Suite). Includes a modern "drill-down" navigation map that updates room availability states instantly.
* **Bespoke Premium UI Components:** Bypasses rigid operating system defaults to implement custom antialiased rounded UI borders (`RoundedCornerBorder`), custom-styled data grids, and interactive grid panels wrapped in a warm tan and gold-beige hospitality theme.
* **Real-Time Billing & Invoice Processing:** Computes total multi-night stay rates dynamically based on room tier prices. Generates a fully detailed, legal text-based receipt directly into the application window upon checkout or check-in.
* **Transactional Integrity:** Utilizes safe SQL operations to ensure that room status updates (`AVAILABLE` $\leftrightarrow$ `OCCUPIED`) happen in tandem with reservation logging, completely eliminating the risk of double-bookings or data desynchronization.

---

## 📂 Database Schema

The system automatically initializes an underlying SQLite database (`hotel_reservation.db`) featuring two interconnected relational structures:

* **`rooms`**: Tracks `room_no` (Primary Key), `category`, `price_per_day`, and `is_available`.
* **`reservations`**: Tracks active and historical logs including guest identity details, allocated room references, specific check-in/expected checkout timestamps, and total settled costs.

---

## 💻 How To Run

1. Ensure you have the **Java Development Kit (JDK)** installed on your machine.
2. Download or add the **SQLite JDBC Driver** (`sqlite-jdbc.jar`) to your project classpath.
3. Compile and execute the main driver class loading the `DashboardScreen` panel.
4. The database file will automatically generate in your local project directory on the first launch.
