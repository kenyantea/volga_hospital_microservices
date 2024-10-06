package com.example.hospitals.service;

import com.example.hospitals.model.Hospital;
import com.example.hospitals.model.Room;
import com.example.hospitals.pojo.request.CreateHospitalRequest;
import com.example.hospitals.repository.HospitalRepository;
import com.example.hospitals.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HospitalService {
    @Autowired
    HospitalRepository hospitalRepository;

    @Autowired
    RoomRepository roomRepository;

    public ResponseEntity<?> getHospitals(int from, int count) {
        Pageable pageable = PageRequest.of(from / count, count);
        Page<Hospital> accounts = hospitalRepository.findAll(pageable);
        if (accounts.isEmpty()) {
            return ResponseEntity.ok("There are no hospitals");
        }
        return ResponseEntity.ok(accounts);
    }

    public Hospital getHospitalById(Long id) {
        return hospitalRepository.findById(id).orElse(null);
    }

    public List<String> getRoomsByHospitalId(Long id) {
        List<Room> rooms = roomRepository.findByHospitalId(id);
        return rooms.stream()
                .map(Room::getRoomNumber)
                .collect(Collectors.toList());
    }

    public Hospital createHospital(CreateHospitalRequest newHospital) {
        if (hospitalRepository.findByName(newHospital.getName()).isPresent()) {
            return null;
        }
        Hospital hospital = new Hospital(newHospital.getName(), newHospital.getAddress(), newHospital.getContactPhone());
        hospital = hospitalRepository.save(hospital);

        Hospital finalHospital = hospital;
        List<Room> rooms = newHospital.getRooms().stream()
                .map(roomName -> new Room(roomName, finalHospital.getId()))
                .collect(Collectors.toList());
        roomRepository.saveAll(rooms);

        return hospital;
    }

    public Hospital updateHospital(Long id, CreateHospitalRequest hospitalDto) {
        Optional<Hospital> hospitalOptional = hospitalRepository.findById(id);
        if (hospitalOptional.isPresent()) {
            Hospital hospital = hospitalOptional.get();
            hospital.setName(hospitalDto.getName());
            hospital.setAddress(hospitalDto.getAddress());
            hospital.setContactPhone(hospitalDto.getContactPhone());

            List<Room> existingRooms = roomRepository.findByHospitalId(hospital.getId());
            List<String> newRoomNumbers = hospitalDto.getRooms();

            List<Room> roomsToRemove = existingRooms.stream()
                    .filter(room -> !newRoomNumbers.contains(room.getRoomNumber()))
                    .collect(Collectors.toList());
            roomRepository.deleteAll(roomsToRemove);

            List<Room> newRooms = newRoomNumbers.stream()
                    .filter(roomNumber -> existingRooms.stream().noneMatch(r -> r.getRoomNumber().equals(roomNumber)))
                    .map(roomNumber -> new Room(roomNumber, hospital.getId()))
                    .collect(Collectors.toList());
            roomRepository.saveAll(newRooms);

            return hospitalRepository.save(hospital);
        }
        return null;
    }

    public void deleteHospital(Long id) {
        Optional<Hospital> hospitalOptional = hospitalRepository.findById(id);
        if (hospitalOptional.isPresent()) {
            Hospital hospital = hospitalOptional.get();
            hospital.setActive(false);
            hospitalRepository.save(hospital);
        }
    }
}
