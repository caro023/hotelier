# Hotelier

This project was developed as part of the *"Laboratorio di Reti"* course during the academic year 2023/2024.

---

## Description

**Hotelier** is a distributed Java application that allows users to register, log in, and review hotels in various cities.  
It features client-server communication over TCP sockets and a UDP multicast system for real-time notifications on hotel rankings.

The system supports multiple concurrent clients and maintains persistent data using JSON files. Hotel scores are computed dynamically based on user ratings and review freshness.

---

## Features

- Register/login/logout   
- Insert and search hotel reviews  
- Dynamic hotel ranking per city  
- Notification of best hotel changes via UDP multicast  
- User badge system for users based on number of reviews  
- Data persistence through JSON serialization  
- Thread-safe concurrent data structures

---

## Technologies

- **Java 21**
- **Gson 2.11.0**
- **TCP & UDP sockets**
- **Multithreading (`ExecutorService`)**
- **Thread-safe collections (`ConcurrentHashMap`, `CopyOnWriteArrayList`)**

---

## Project Structure

```
├── MainServer.java        # Entry point for the server
├── MainClient.java        # Entry point for the client
├── JsonHandler.java       # Handles JSON data (read/write)
├── updateBestHotel.java   # Notifies clients of ranking changes
├── ClientHandler.java     # Handles each client session(multi-threaded)
├── ListenMulticast.java   # Listens to UDP multicast messages
```

---

## How to Run

### From JAR files

```bash
# Start the server
java -jar MainServer.jar

# In another terminal, start the client
java -jar MainClient.jar
```

###  From source files

#### Server

```bash
javac -cp .;gson/* MainServer.java
java -cp .;gson/* MainServer
```

#### Client

```bash
javac MainClient.java
java MainClient
```
 **Note:** Run the **server** before starting any clients.

---

## Configuration Files

- `server.properties` → ports, review expiration time, max scores, etc.  
- `client.properties` → server ports and multicast info  
- `city.txt` → list of cities available for hotel registration  
- JSON files → data persistence for users, reviews, and hotels

---

## Available commands for users

| Command Format | Description |
|----------------|-------------|
| `Register <username> <password>` | Create a new user |
| `Login <username> <password>` | Log in to the system |
| `SearchHotel "<hotel>" "<city>"` | Search for a hotel |
| `InsertReview "<hotel>" "<city>" x x x x x` | Submit a new review. x must be between 0 and 5 |
| `searchAllHotels "city"`| Search for all the hotels in the city ordered based on the scores|
| `ShowMyBadges` | Show badge status |
| `Logout` / `Exit` | End session or close client |

---
