package com.carenet.dto;

import com.carenet.model.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookAppointmentRequest {
    public Long                        doctorId;
    public LocalDate                   date;
    public LocalTime                   time;
    public Appointment.AppointmentType type;
    public String                      notes;
}
