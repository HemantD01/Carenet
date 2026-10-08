package com.carenet.repository;

import com.carenet.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientAbhaIdOrderByUploadDateDesc(String abhaId);
    List<MedicalRecord> findByPatient_PatientIdOrderByUploadDateDesc(Long patientId);
    List<MedicalRecord> findByDoctor_DoctorIdOrderByUploadDateDesc(Long doctorId);
    long countByPatient_PatientId(Long patientId);
    long countByDoctor_DoctorId(Long doctorId);
}
