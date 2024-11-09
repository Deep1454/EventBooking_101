## Introduction
The Event Booking Project is a microservices-based application developed in Java that facilitates the booking of events and rooms. It allows users to create events, manage bookings, and handle approvals through a secure and efficient interface. In this system, only administrators have permission to approve bookings, ensuring that all requests are verified and managed appropriately. This project is designed for users and organizations that require a reliable way to organize and manage events in various venues.

## features
- User Authentication
- Room Booking
- Event Management
- Approval Workflow 
- User Roles

## Technologies Used
- Java
- Spring Boot
- Gradle-Kotlin
- PostgreSQL
- MongoDB
- Docker
- pgAdmin

## Architecture
This project follows a microservices architecture, where each service is responsible for a specific business capability. The services communicate through REST APIs and enabling scalability.
- UserService: Manages user accounts and authentication.
- RoomService: Handles room availability and bookings.
- BookingService: Manages the booking process and links users to rooms.
- EventService: Manage events and their details.
- ApprovalService: Manages the approval process for event requests.

## Installation
To set up the project locally, follow these steps:
1. Clone git repository 
       — bash:
       git clone https://github.com/Deep1454/EventBooking_101.git
2. Navigate into the project directory:
       cd EventBooking_101
3. Build the project using Gradle:
       ./gradlew build

## Microservice Overview 
- UserService: Handles user registration, authentication, and profile management.
- RoomService: Manage room availability and booking details.
- BookingService: Coordinates bookings, checking user validity, and confirming reservations.
- EventService: Creates and manages events associated with room bookings.
- ApprovalService:  Facilitates the approval process for events and manages user roles and permissions.

## API Endpoints
UserService:
- Create User:  POST  /api/users
- Get User by ID:  GET  /api/users/{id}
- Get User by Email:  GET  /api/users/email
- Get All Users:  GET  /api/users
- Update User:  PUT  /api/users/{id}
- Delete User:  DELETE  /api/users/{id}
- Check User Role:  GET  /api/users/{id}/role
- Check User type:  GET  /api/users/{id}/usertype
  
RoomService:
- Create Room:  POST  /api/rooms
- Get Room by ID:  GET  /api/rooms/{id}
- Get Room by RoomID:  GET  /api/rooms/{roomId}
- Get All Rooms:  GET  /api/rooms
- Update Room:  PUT  /api/rooms/{id}
- Delete Room:  DELETE  /api/rooms/{id}
- Check Room Availability:  GET  /api/rooms/{id}/availability
- Check Room Availability by RoomID:  GET  /api/rooms/{id}/{roomId}/availability
- Get Available Rooms:  GET  /api/rooms/available

EventService:
- Create Event:  POST  /api/events
- Get Event by ID: GET  /api/events/{id}
- Get All Events:  GET  /api/events
- Update Events:  PUT  /api/events/{id}
- Delete Events:  DELETE  /api/events/{id}
  
BookingService:
- Create Booking:  POST  /api/bookings
- Get All Bookings:  GET  /api/bookings
- Get Booking by Id:  GET  /api/bookings/{id}
- Delete Booking:  DELETE  /api/bookings/{id}
  
ApprovalService:
- Create Approval:  POST  /api/approvals
- Get Approval by Id:  GET  /api/approvals/{id}
- Get All Approvals:  GET  /api/approvals
- Delete Approvals:  DELETE  /api/approvals/{id}

## Database Structures
- Users: Stores user information, including roles and authentication details.
- Rooms: Contains details of rooms available for booking.
- Events: Manages event data linked to room bookings.
- Approvals: Record approval statuses and details related to events.
- Bookings: Current status of the booking.

