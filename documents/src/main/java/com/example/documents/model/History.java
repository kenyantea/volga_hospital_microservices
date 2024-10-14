package com.example.documents.model;

import com.example.documents.pojo.request.HistoryRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime time;
    private Long patientId;
    private Long hospitalId;
    private Long doctorId;
    private String room;
    private String data;

    public History() {}

    public History(HistoryRequest historyRequest) {
        this.time = historyRequest.getTime();
        this.patientId = historyRequest.getPatientId();
        this.doctorId = historyRequest.getDoctorId();
        this.hospitalId = historyRequest.getHospitalId();
        this.room = historyRequest.getRoom();
        this.data = historyRequest.getData();
    }
}
