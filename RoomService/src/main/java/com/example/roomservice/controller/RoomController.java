package com.example.roomservice.controller;

import com.example.roomservice.model.Room;
import com.example.roomservice.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable int id) {
        Optional<Room> room = roomService.getRoomById(id);
        return room.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/roomId/{roomId}")
    public ResponseEntity<Room> getRoomByRoomId(@PathVariable String roomId) {
        Optional<Room> room = roomService.getRoomByRoomId(roomId);
        return room.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room savedRoom = roomService.saveRoom(room);
        return ResponseEntity.created(URI.create("/api/rooms/" + savedRoom.getId())).body(savedRoom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable int id, @RequestBody Room roomDetails) {
        Room updatedRoom = roomService.updateRoom(id, roomDetails);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable int id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkRoomAvailability(@PathVariable int id) {
        return roomService.getRoomById(id)
                .map(room -> ResponseEntity.ok(room.isAvailability()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/roomId/{roomId}/availability")
    public ResponseEntity<Boolean> checkRoomAvailabilityByRoomId(@PathVariable String roomId) {
        return roomService.getRoomByRoomId(roomId)
                .map(room -> ResponseEntity.ok(room.isAvailability()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    public List<Room> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }
}
