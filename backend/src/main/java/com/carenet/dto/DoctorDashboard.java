package com.carenet.dto;

public class DoctorDashboard {
    public Long   todayAppointments;
    public Long   totalPatients;
    public Long   totalReportsUploaded;
    public Double rating;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final DoctorDashboard d = new DoctorDashboard();
        public Builder todayAppointments(Long v)   { d.todayAppointments = v; return this; }
        public Builder totalPatients(Long v)        { d.totalPatients = v; return this; }
        public Builder totalReportsUploaded(Long v) { d.totalReportsUploaded = v; return this; }
        public Builder rating(Double v)             { d.rating = v; return this; }
        public DoctorDashboard build()              { return d; }
    }
}
