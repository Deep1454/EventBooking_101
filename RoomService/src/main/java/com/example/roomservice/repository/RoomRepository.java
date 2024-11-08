package com.example.roomservice.repository;

import com.example.roomservice.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByAvailability(boolean availability);
    List<Room> findByCapacityGreaterThan(int capacity);
    Optional<Room> findByRoomName(String roomName);
    Optional<Room> findByRoomId(String roomId);
}
