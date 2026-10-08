package com.carenet.dto;

public class PatientDashboard {
    public Long totalRecords;
    public Long upcomingAppointments;
    public Long totalDoctorsConsulted;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PatientDashboard d = new PatientDashboard();
        public Builder totalRecords(Long v)          { d.totalRecords = v; return this; }
        public Builder upcomingAppointments(Long v)  { d.upcomingAppointments = v; return this; }
        public Builder totalDoctorsConsulted(Long v) { d.totalDoctorsConsulted = v; return this; }
        public PatientDashboard build()              { return d; }
    }
}
