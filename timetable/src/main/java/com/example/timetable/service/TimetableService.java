package com.example.timetable.service;

import com.example.timetable.model.Appointment;
import com.example.timetable.model.TimetableEntry;
import com.example.timetable.pojo.request.TimetableRequest;
import com.example.timetable.pojo.response.UserResponse;
import com.example.timetable.repository.AppointmentRepository;
import com.example.timetable.repository.TimetableRepository;
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
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class TimetableService {
    @Autowired
    TimetableRepository timetableRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    RestTemplate restTemplate = new RestTemplate();

    private static final DateTimeFormatter ISO_8601_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public TimetableEntry createTimetableEntry(TimetableRequest timetableRequest) {
        validateTimetableRequest(timetableRequest);

        if (isSpecialistAvailable(timetableRequest.getDoctorId(),
                timetableRequest.getFrom(), timetableRequest.getTo())) {
            TimetableEntry timetableEntry = new TimetableEntry(
                    timetableRequest.getHospitalId(),
                    timetableRequest.getDoctorId(),
                    parseDateTime(timetableRequest.getFrom()),
                    parseDateTime(timetableRequest.getTo()),
                    timetableRequest.getRoom()
            );
            return timetableRepository.save(timetableEntry);
        } else {
            throw new IllegalArgumentException("This time for this doctor is not available.");
        }
    }

    public TimetableEntry updateTimetableEntry(Long id, TimetableRequest timetableRequest) {

        validateTimetableRequest(timetableRequest);

        Optional<TimetableEntry> existingEntryOptional = timetableRepository.findById(id);


        if (existingEntryOptional.isPresent()) {
            TimetableEntry existingEntry = existingEntryOptional.get();

            if (Objects.equals(existingEntry.getDoctorId(), timetableRequest.getDoctorId())
                && Objects.equals(existingEntry.getHospitalId(),timetableRequest.getHospitalId())
                && Objects.equals(existingEntry.getStart(), parseDateTime(timetableRequest.getFrom()))
                && Objects.equals(existingEntry.getEnd_time(), parseDateTime(timetableRequest.getTo()))
                && Objects.equals(existingEntry.getRoom(), timetableRequest.getRoom())) {
                return new TimetableEntry(timetableRequest.getHospitalId(),
                        timetableRequest.getDoctorId(),
                        parseDateTime(timetableRequest.getFrom()),
                        parseDateTime(timetableRequest.getTo()),
                        timetableRequest.getRoom());
            }

            if (isSpecialistAvailable(timetableRequest.getDoctorId(),
                    timetableRequest.getFrom(), timetableRequest.getTo())) {
                existingEntry.setHospitalId(timetableRequest.getHospitalId());
                existingEntry.setDoctorId(timetableRequest.getDoctorId());
                existingEntry.setStart(parseDateTime(timetableRequest.getFrom()));
                existingEntry.setEnd_time(parseDateTime(timetableRequest.getTo()));
                existingEntry.setRoom(timetableRequest.getRoom());
                return timetableRepository.save(existingEntry);
            }
        } else {
            throw new IllegalArgumentException("This time for this doctor is not available.");
        }
        return null;
    }

    public void deleteTimetableEntry(Long id) {
        Optional<TimetableEntry> timetableEntry = timetableRepository.findById(id);
        if (timetableEntry.isPresent()) {
            timetableEntry.get().set_active(false); // Отмечаем запись как неактивную
            timetableRepository.save(timetableEntry.get());
        } else {
            throw new IllegalArgumentException("This appointment doesn't exist.");
        }
    }

    public void deleteTimetableEntriesByDoctor(Long doctorId) {
        timetableRepository.findByDoctorId(doctorId).forEach(te -> {
            te.set_active(false);
            timetableRepository.save(te);
        });
    }

    public void deleteTimetableEntriesByHospital(Long hospitalId) {
        timetableRepository.findByHospitalId(hospitalId).forEach(te -> {
            te.set_active(false);
            timetableRepository.save(te);
        });
    }

    public LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    private void validateTimetableRequest(TimetableRequest timetableRequest) {
        if (!isValidDateTime(timetableRequest.getFrom()) || !isValidDateTime(timetableRequest.getTo())) {
            throw new IllegalArgumentException("Invalid date time format. Use ISO 8601 format (e.g., '2024-04-25T11:30:00Z')");
        }

        // Проверка кратности 30 минут
        if (!isMinutesMultipleOf30(timetableRequest.getFrom()) || !isMinutesMultipleOf30(timetableRequest.getTo())) {
            throw new IllegalArgumentException("Minutes must be a multiple of 30.");
        }

        // Проверка секунд (должны быть 0)
        if (!isSecondsZero(timetableRequest.getFrom()) || !isSecondsZero(timetableRequest.getTo())) {
            throw new IllegalArgumentException("Seconds must be 0.");
        }

        // Проверка to > from
        if (parseDateTime(timetableRequest.getFrom()).isAfter(parseDateTime(timetableRequest.getTo()))) {
            throw new IllegalArgumentException("Time 'end' must be after time 'start'.");
        }

        // Проверка разницы между to и from
        if (ChronoUnit.HOURS.between(parseDateTime(timetableRequest.getFrom()), parseDateTime(timetableRequest.getTo())) > 12) {
            throw new IllegalArgumentException("The difference between 'end' and 'start' must be less than 12 hours.");
        }
    }

    private boolean isSpecialistAvailable(Long specialist, String start, String end) {
        LocalDateTime startDateTime = parseDateTime(start),
                endDateTime = parseDateTime((end));
        List<TimetableEntry> existingAppointments = timetableRepository.findByDoctorIdAndTimeOverlap(specialist, startDateTime, endDateTime);
        return existingAppointments.isEmpty();
    }

    private static boolean isValidDateTime(String dateTimeString) {
        try {
            LocalDateTime.parse(dateTimeString, ISO_8601_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isMinutesMultipleOf30(String dateTimeString) {
        if (!isValidDateTime(dateTimeString)) {
            return false;
        }
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, ISO_8601_FORMATTER);
        return dateTime.getMinute() % 30 == 0;
    }

    private static boolean isSecondsZero(String dateTimeString) {
        if (!isValidDateTime(dateTimeString)) {
            return false;
        }
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, ISO_8601_FORMATTER);
        return dateTime.getSecond() == 0;
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
        } catch (JsonSyntaxException e) {
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

    public List<TimetableEntry> getTimetableByHospital(Long id, String from, String to) {
        if (from != null && to != null) {
            return timetableRepository.findByHospitalIdAndStartGreaterThanEqualAndEndLessThanEqual(id, parseDateTime(from), parseDateTime(to));
        } else if (from != null) {
            return timetableRepository.findByHospitalIdAndStartAfter(id, parseDateTime(from));
        } else if (to != null) {
            return timetableRepository.findByHospitalIdAndEndBefore(id, parseDateTime(to));
        } else {
            return timetableRepository.findByHospitalId(id);
        }
    }

    public Object getTimetableByDoctor(Long id, String from, String to) {
        if (from != null && to != null) {
            return timetableRepository.findByDoctorIdAndStartGreaterThanEqualAndEndLessThanEqual(id, parseDateTime(from), parseDateTime(to));
        } else if (from != null) {
            return timetableRepository.findByDoctorIdAndStartAfter(id, parseDateTime(from));
        } else if (to != null) {
            return timetableRepository.findByDoctorIdAndEndBefore(id, parseDateTime(to));
        } else {
            return timetableRepository.findByDoctorId(id);
        }
    }

    public List<TimetableEntry> getTimetableByHospitalAndRoom(Long hospitalId, String room, String fromStr, String toStr) {
        LocalDateTime from, to;
        try {
            from = parseDateTime(fromStr);
            to = parseDateTime(toStr);
        } catch (Exception e) {
            from = null;
            to = null;
        }
        if (from != null && to != null) {
            return timetableRepository.findByHospitalIdAndRoomAndTimeBetween(hospitalId, room, from, to);
        } else if (from != null) {
            return timetableRepository.findByHospitalIdAndRoomAndStartAfter(hospitalId, room, from);
        } else if (to != null) {
            return timetableRepository.findByHospitalIdAndRoomAndEndBefore(hospitalId, room, to);
        } else {
            return timetableRepository.findByHospitalIdAndRoom(hospitalId, room);
        }
    }

    public List<?> getAppointments(Long id) {
        List<TimetableEntry> timetable = timetableRepository.getTimetableEntryById(id);
        List<String> availableAppointments = new ArrayList<>();

        for (TimetableEntry entry : timetable) {
            LocalDateTime from = entry.getStart();
            LocalDateTime to = entry.getEnd_time();

            while (from.isBefore(to)) {
                availableAppointments.add(from.format(DateTimeFormatter.ISO_DATE_TIME));
                from = from.plusMinutes(30);
            }
        }

        return availableAppointments;
    }

    public void bookAppointment(Long id, Appointment appointment) {
        Optional<Appointment> existingAppointment = appointmentRepository.findByTimeAndTimetableId(appointment.getTime(), id);
        if (existingAppointment.isPresent()) {
            throw new IllegalArgumentException("Appointment at this time is already booked.");
        }

        appointment.setId(id);
        appointmentRepository.save(appointment);
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

    public void cancelAppointment(Long id, String token) {
        Long userId = getUserIdFromToken(token);
        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (appointment.isEmpty()) {
            throw new IllegalArgumentException("The appointment already doesn't exist.");
        }
        if (Objects.equals(appointment.get().getUser_id(), userId) ||
                isAuthenticated(token).stream().anyMatch
                        (role -> role.contains("ROLE_ADMIN") || role.contains("ROLE_MANAGER"))) {
            appointmentRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Only administrators, managers, and the patient can delete this appointment.");
        }

    }

    public boolean isExistingTimetable(Long id) {
        Optional<TimetableEntry> timetableEntry = timetableRepository.findById(id);
        return timetableEntry.isPresent();
    }
}
