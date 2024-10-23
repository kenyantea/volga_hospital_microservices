package com.example.timetable.repository;

import com.example.timetable.model.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableEntry, Long> {

    @Query("SELECT te FROM TimetableEntry te " +
            "WHERE te.doctorId = :doctorId " +
            "AND ((:from BETWEEN te.start AND te.end_time) " +
            "OR (:to BETWEEN te.start AND te.end_time) " +
            "OR (:from <= te.start AND :to >= te.end_time))")
    List<TimetableEntry> findByDoctorIdAndTimeOverlap(Long doctorId, LocalDateTime from, LocalDateTime to);

    List<TimetableEntry> findByDoctorId(Long doctorId);
    List<TimetableEntry> findByHospitalId(Long hospitalId);
    @Query("SELECT te FROM TimetableEntry te WHERE te.doctorId = :doctorId AND te.start >= :from AND te.end_time<= :to")
    List<TimetableEntry> findByDoctorIdAndStartGreaterThanEqualAndEndLessThanEqual(Long doctorId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT te FROM TimetableEntry te WHERE te.hospitalId = :hospitalId AND te.start >= :from AND te.end_time<= :to")
    List<TimetableEntry> findByHospitalIdAndStartGreaterThanEqualAndEndLessThanEqual(Long hospitalId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT te FROM TimetableEntry te WHERE te.doctorId = :doctorId AND te.start >= :from")
    List<TimetableEntry> findByDoctorIdAndStartAfter(Long doctorId, LocalDateTime from);

    @Query("SELECT te FROM TimetableEntry te WHERE te.hospitalId = :hospitalId AND te.start >= :from")
    List<TimetableEntry> findByHospitalIdAndStartAfter(Long hospitalId, LocalDateTime from);

    @Query("SELECT te FROM TimetableEntry te WHERE te.doctorId = :doctorId AND te.end_time<= :to")
    List<TimetableEntry> findByDoctorIdAndEndBefore(Long doctorId, LocalDateTime to);

    @Query("SELECT te FROM TimetableEntry te WHERE te.hospitalId = :hospitalId AND te.end_time<= :to")
    List<TimetableEntry> findByHospitalIdAndEndBefore(Long hospitalId, LocalDateTime to);

    @Query("SELECT te FROM TimetableEntry te WHERE te.hospitalId = :hospitalId AND te.room = :room AND te.start >= :from AND te.end_time<= :to")
    List<TimetableEntry> findByHospitalIdAndRoomAndTimeBetween(Long hospitalId, String room, LocalDateTime from, LocalDateTime to);

    @Query("SELECT te FROM TimetableEntry te WHERE te.hospitalId = :hospitalId AND te.room = :room AND te.start >= :from")
    List<TimetableEntry> findByHospitalIdAndRoomAndStartAfter(Long hospitalId, String room, LocalDateTime from);

    @Query("SELECT te FROM TimetableEntry te WHERE te.hospitalId = :hospitalId AND te.room = :room AND te.end_time<= :to")
    List<TimetableEntry> findByHospitalIdAndRoomAndEndBefore(Long hospitalId, String room, LocalDateTime to);

    @Query("SELECT te FROM TimetableEntry te WHERE te.hospitalId = :hospitalId AND te.room = :room")
    List<TimetableEntry> findByHospitalIdAndRoom(Long hospitalId, String room);

    List<TimetableEntry> getTimetableEntryById(Long id);
}


