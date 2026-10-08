package com.carenet.repository;

import com.carenet.model.Appointment;
import com.carenet.model.Appointment.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient_PatientIdOrderByAppointmentDateDesc(Long patientId);
    List<Appointment> findByPatient_PatientIdAndStatus(Long patientId, AppointmentStatus status);

    List<Appointment> findByDoctor_DoctorIdOrderByAppointmentDateAsc(Long doctorId);
    List<Appointment> findByDoctor_DoctorIdAndStatus(Long doctorId, AppointmentStatus status);
    List<Appointment> findByDoctor_DoctorIdAndAppointmentDate(Long doctorId, LocalDate date);

    long countByDoctor_DoctorIdAndAppointmentDate(Long doctorId, LocalDate date);
}
