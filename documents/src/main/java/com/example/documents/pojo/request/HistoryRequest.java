package com.example.documents.pojo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HistoryRequest {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime time;
    private Long patientId;
    private Long hospitalId;
    private Long doctorId;
    private String room;
    private String data;
}
