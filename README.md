
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
