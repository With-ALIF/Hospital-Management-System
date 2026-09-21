package com.hospital.ui;

import com.hospital.model.Doctor;
import com.hospital.model.TimeSlot;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SlotParser {
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static void parseAndAdd(Doctor doctor, String slotStr) {
        if (slotStr == null || slotStr.isBlank()) return;
        String[] parts = slotStr.replace("→", "-").replace("to", "-").split("-");
        if (parts.length == 2) {
            try {
                LocalTime start = LocalTime.parse(parts[0].trim(), TIME_FORMATTER);
                LocalTime end = LocalTime.parse(parts[1].trim(), TIME_FORMATTER);
                doctor.addDutySlot(new TimeSlot(start, end));
            } catch (Exception ignored) {}
        }
    }
}
