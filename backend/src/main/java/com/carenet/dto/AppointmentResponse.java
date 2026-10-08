package com.carenet.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentResponse {
    public Long          appointmentId;
    public String        patientName;
    public String        patientAbhaId;
    public String        doctorName;
    public String        doctorSpecialization;
    public String        hospital;
    public LocalDate     date;
    public LocalTime     time;
    public String        type;
    public String        status;
    public String        notes;
    public LocalDateTime createdAt;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final AppointmentResponse r = new AppointmentResponse();
        public Builder appointmentId(Long v)          { r.appointmentId = v; return this; }
        public Builder patientName(String v)          { r.patientName = v; return this; }
        public Builder patientAbhaId(String v)        { r.patientAbhaId = v; return this; }
        public Builder doctorName(String v)           { r.doctorName = v; return this; }
        public Builder doctorSpecialization(String v) { r.doctorSpecialization = v; return this; }
        public Builder hospital(String v)             { r.hospital = v; return this; }
        public Builder date(LocalDate v)              { r.date = v; return this; }
        public Builder time(LocalTime v)              { r.time = v; return this; }
        public Builder type(String v)                 { r.type = v; return this; }
        public Builder status(String v)               { r.status = v; return this; }
        public Builder notes(String v)                { r.notes = v; return this; }
        public Builder createdAt(LocalDateTime v)     { r.createdAt = v; return this; }
        public AppointmentResponse build()            { return r; }
    }
}
