package com.example.hospitals.repository;

import com.example.hospitals.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHospitalId(Long hospitalId);
}
