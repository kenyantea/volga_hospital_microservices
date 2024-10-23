package com.example.hospitals.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "hospitals")
@NoArgsConstructor
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String address;
    private String contactPhone;
    private boolean isActive = false;

    public Hospital(String name, String address, String contactPhone) {
        this.name = name;
        this.address = address;
        this.contactPhone = contactPhone;
        isActive = true;
    }
}
