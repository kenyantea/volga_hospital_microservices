package com.example.hospitals.pojo.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateHospitalRequest {
    private String name;
    private String address;
    private String contactPhone;
    private List<String> rooms;
}
