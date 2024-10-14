package com.example.documents.service;

import com.example.documents.model.History;
import com.example.documents.pojo.response.AccountRolesResponse;
import com.example.documents.pojo.response.UserResponse;
import com.example.documents.repository.HistoryRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class HistoryService {
    @Autowired
    HistoryRepository historyRepository;

    RestTemplate restTemplate = new RestTemplate();

    public Set<String> isAuthenticated(String token) {
        String url = "http://localhost:8080/api/Authentication/Validate?accessToken=" +
                token.substring(7);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            Set<String> roles = new HashSet<>();

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                if (responseBody != null && !responseBody.isEmpty()) {
                    String[] parts = responseBody.split(",");
                    roles.addAll(Arrays.asList(parts));
                    return roles;
                }
            }
            return null;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }

    public Long getUserIdFromToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8080/api/Accounts/Me",
                HttpMethod.GET,
                requestEntity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Gson gson = new Gson();
            UserResponse userResponse = gson.fromJson(response.getBody(), UserResponse.class);
            return userResponse.getId();
        } else {
            throw new RuntimeException("Error getting user from token: " + response.getStatusCode());
        }
    }

    public List<History> getHistoryByAccountId(Long id, String token) {
        Long userId = getUserIdFromToken(token);
        if (Objects.equals(id, userId) ||
                isAuthenticated(token).stream().anyMatch
                        (role -> role.contains("ROLE_DOCTOR"))) {
            return historyRepository.findByPatientId(id);
        } else {
            throw new IllegalArgumentException("Only doctors and the patient themselves can get patient's history.");
        }
    }

    public History getHistoryById(Long id, String token) {
        Long userId = getUserIdFromToken(token);
        if (Objects.equals(id, userId) ||
                isAuthenticated(token).stream().anyMatch
                        (role -> role.contains("ROLE_DOCTOR"))) {
            if (historyRepository.findById(id).isPresent()) {
                return historyRepository.findById(id).get();
            }
            return null;
        } else {
            throw new IllegalArgumentException("Only doctors and the patient themselves can get patient's history.");
        }
    }

    public History createHistory(History history, String token) {
        if (!isAuthenticated(token).stream().anyMatch
                        (role -> role.contains("ROLE_DOCTOR") || role.contains("ROLE_ADMIN") || role.contains("ROLE_MANAGER"))) {
            throw new IllegalArgumentException("Only doctors, managers, and admins can create a history entry.");
        } else if (!existUser(history.getPatientId(),token)) {
            throw new IllegalArgumentException("The patient's account must have role of ROLE_USER");
        } else if (!checkIds(history.getHospitalId(),history.getDoctorId(),
                history.getRoom(),token)) {
            throw new IllegalArgumentException("Please check that hospital, doctor, and room are entered correctly.");
        }
        return historyRepository.save(history);
    }

    public History updateHistory(Long id, History history, String token) {
        history.setId(id);
        if (!isAuthenticated(token).stream().anyMatch
                (role -> role.contains("ROLE_DOCTOR") || role.contains("ROLE_ADMIN") || role.contains("ROLE_MANAGER"))) {
            throw new IllegalArgumentException("Only doctors, managers, and admins can edit a history entry.");
        } else if (!existUser(history.getPatientId(),token)) {
            throw new IllegalArgumentException("The patient's account must have role of ROLE_USER");
        } else if (!checkIds(history.getHospitalId(),history.getDoctorId(),
                history.getRoom(),token)) {
            throw new IllegalArgumentException("Please check that hospital, doctor, and room are entered correctly.");
        }
        return historyRepository.save(history);
    }


    public boolean existUser(Long id, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8080/api/Accounts/" + id,
                HttpMethod.GET,
                requestEntity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Gson gson = new Gson();
            AccountRolesResponse.Account[] accounts = gson.fromJson(response.getBody(), AccountRolesResponse.Account[].class);
            for (AccountRolesResponse.Account account : accounts) {
                if (account.getName().contains("ROLE_USER")) {
                    return true;
                }
            }
            return false;
        } else if (response.getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)
                || response.getStatusCode().isSameCodeAs(HttpStatus.FORBIDDEN)) {
            throw new RuntimeException("Access denied.");
        } else {
            throw new RuntimeException("Error getting user from token: " + response.getStatusCode());
        }
    }



    public boolean checkIds(Long hospitalId, Long doctorId, String roomId, String token) {
        // Проверка больницы
        ResponseEntity<String> hospitalResponse = getHospitalDataById(hospitalId, token);
        if (!hospitalResponse.getStatusCode().is2xxSuccessful() || !isHospitalActive(hospitalResponse.getBody())) {
            return false;
        }

        // Проверка врача
        ResponseEntity<String> doctorResponse = getDoctorDataById(doctorId, token);
        if (!doctorResponse.getStatusCode().is2xxSuccessful() || !isDoctorActive(doctorResponse.getBody())) {
            return false;
        }


        // Проверка комнаты
        ResponseEntity<String> roomsResponse = getRoomsForHospital(hospitalId, token);
        if (!roomsResponse.getStatusCode().is2xxSuccessful() || !isRoomInList(roomsResponse.getBody(), roomId)) {
            return false;
        }

        return true;
    }

    public ResponseEntity<String> getHospitalDataById(Long hospitalId, String token) {
        String url = "http://localhost:8081/api/Hospitals/" + hospitalId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<String> getDoctorDataById(Long doctorId, String token) {
        String url = "http://localhost:8080/api/Doctors/" + doctorId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<String> getRoomsForHospital(Long hospitalId, String token) {
        String url = "http://localhost:8081/api/Hospitals/" + hospitalId + "/Rooms";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public boolean isRoomInList(String roomsData, String roomId) {
        if (roomsData == null || roomId == null) {
            return false;
        }
        Gson gson = new Gson();
        try {
            JsonArray roomsArray = gson.fromJson(roomsData, JsonArray.class);
            if (roomsArray != null && !roomsArray.isEmpty()) {
                for (int i = 0; i < roomsArray.size(); i++) {
                    String room = roomsArray.get(i).getAsString();
                    if (room.equals(roomId)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return false;
        }
        return false;
    }



    public boolean isHospitalActive(String hospitalData) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(hospitalData, JsonObject.class);
        return jsonObject.get("active").getAsBoolean();
    }

    public boolean isDoctorActive(String doctorData) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(doctorData, JsonObject.class);
        return jsonObject.get("active").getAsBoolean();
    }
}
