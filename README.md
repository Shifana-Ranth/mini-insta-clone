# 📸 Mini Insta Clone — Event-Driven Microservices Backend

## 🚀 Overview

This project is a **scalable Instagram-like backend system** built using a **microservices architecture** with an **event-driven design powered by Kafka**.

The system is designed to handle high concurrency, asynchronous workflows, and real-time updates across services such as posts, likes, follows, notifications, and feeds.

---

## 🧠 Core Idea

Instead of tightly coupling services, this system uses **Apache Kafka as the central communication backbone**.

All major actions (post creation, likes, follows, etc.) are treated as **events**, which are published to Kafka topics and consumed by multiple services independently.

👉 This ensures:

* Loose coupling
* High scalability
* Fault tolerance
* Asynchronous processing

---

## 🏗️ Architecture

### Microservices Used:

* User Service
* Post Service
* Like Service
* Follow Service
* Feed Service
* Notification Service
* Search Service

Each service:

* Is independently deployable
* Has its own database
* Communicates via Kafka (event-driven) + REST (when needed)

---

## 🔁 Event Flow (High-Level)

### 🧑 User Events

* User-related actions are published to **USER-EVENT topic**
* Notification Service consumes these events to trigger notifications

---

### 👥 Follow / Unfollow Flow

* Follow Service produces events to **USER-FOLLOW topic**
* Follow Service updates its DB based on event type (follow/unfollow)
* Notification Service consumes this event to notify users

---

### 📝 Post Flow

* Post Service publishes events to **USER-POST topic** when:

  * Post is created
  * Post is deleted
* Post Service persists data in its database

---

### ❤️ Like Flow

* Like Service:

  * Processes like/unlike actions
  * Publishes like events
* Post Service consumes like events to:

  * Update like count in DB
* Feed Service updates cached posts accordingly
* Notification Service sends like notifications

---

### 📰 Feed Service (Performance Optimization)

* Uses **Redis caching**
* Caches:

  * Followers list
  * User feed posts
* Listens to:

  * Post Service events
  * Like Service events
* Updates cache in real-time

---

### 🔍 Search Service

* Uses **Apache Lucene**
* Enables efficient searching of users

---

## ⚙️ Tech Stack
* **Backend:** Java (Servlet)
* **Architecture:** Microservices
* **Communication:**
  * Apache Kafka (event-driven)
  * REST APIs
* **Caching:** Redis
* **Search:** Apache Lucene
* **Databases:** Separate DB per service

---

## 🧩 Real-World System Design Challenges

### 1. Feed Scaling (Celebrity Problem)

**Problem:**
Fan-out on write works well for normal users but breaks down for users with millions of followers (celebrity accounts), causing massive write amplification.

**Solution:**
A hybrid feed generation strategy was used:

* **Normal users:** Fan-out on write (precompute feeds and push to cache)
* **Celebrity users:** Fan-out on read (compute feed dynamically during request)

**Implementation:**

* Precomputed feeds cached in Redis for regular users
* Dynamic feed merging at request time for high-follower users
* Reduced unnecessary writes and improved scalability

---

### 2. Like System at Scale

**Problem:**
Handling high write throughput during viral events (e.g., thousands of likes per second on a single post).

**Solution:**

* Asynchronous processing using Kafka
* Eventual consistency model
* Cache-first read optimization

**Implementation:**

* Like events are published to Kafka topics
* Multiple consumers process events independently
* Post Service updates like counts asynchronously
* Redis used for fast retrieval of like counts

---

### 3. Notification Overload

**Problem:**
Sending a notification for every event leads to poor user experience and notification fatigue.

**Solution:**

* Filtering and prioritization logic applied before sending notifications
* Avoid unnecessary or low-value notifications

**Future Improvements:**

* ML-based notification ranking system
* Personalized notification delivery based on user behavior

---

## 🔥 Key Design Decisions

### 1. Event-Driven Architecture (Kafka Everywhere)

Almost all interactions are handled via Kafka to:

* Reduce direct service dependency
* Enable async workflows
* Improve scalability

---

### 2. Database per Service

Each microservice owns its data:

* Avoids tight coupling
* Allows independent scaling

---

### 3. Redis for Feed Optimization

Feed generation is expensive → optimized using caching:

* Faster reads
* Reduced DB load

---

### 4. Hybrid Communication

* Kafka → async communication
* REST → synchronous calls when needed

---

## 📈 Scalability Considerations

* Services can scale independently
* Kafka partitions allow parallel processing
* Redis reduces database bottlenecks
* Event-driven design avoids blocking operations

---

## ⚠️ Limitations / Trade-offs

* Eventual consistency (not immediate consistency)
* Increased system complexity
* Debugging across services can be harder
* Requires proper Kafka topic management

---

## ▶️ How to Run

1. Start Kafka and Zookeeper
2. Start Redis
3. Run each microservice individually
4. Ensure all required topics are created and its consumer are started
5. Trigger APIs via Postman / client

---

## 👨‍💻 Author

Shifana Ranth M

---

## 🎯 Final Note

This project demonstrates:

* Strong understanding of distributed systems
* Event-driven architecture
* Backend scalability patterns

---
