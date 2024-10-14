package com.example.documents.repository;

import com.example.documents.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByPatientId(Long patientId);
    Optional<History> findById(Long id);
}
