package com.carenet.service;

import com.carenet.dto.*;
import com.carenet.exception.BadRequestException;
import com.carenet.exception.ResourceNotFoundException;
import com.carenet.model.Doctor;
import com.carenet.model.MedicalRecord;
import com.carenet.model.Patient;
import com.carenet.repository.DoctorRepository;
import com.carenet.repository.MedicalRecordRepository;
import com.carenet.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepository;
    private final PatientRepository       patientRepository;
    private final DoctorRepository        doctorRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public MedicalRecordService(MedicalRecordRepository recordRepository,
                                PatientRepository patientRepository,
                                DoctorRepository doctorRepository) {
        this.recordRepository  = recordRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository  = doctorRepository;
    }

    // ── Upload Report (Doctor) ────────────────────────────────────────────────

    @Transactional
    public MedicalRecordResponse uploadRecord(Long doctorId,
                                              UploadRecordRequest req,
                                              MultipartFile file) throws IOException {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found."));

        Patient patient = patientRepository.findByAbhaId(req.patientAbhaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No patient found with ABHA ID: " + req.patientAbhaId));

        String fileName = null;
        String filePath = null;

        if (file != null && !file.isEmpty()) {
            validateFile(file);
            fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            filePath = dir.resolve(fileName).toString();
            Files.copy(file.getInputStream(), Paths.get(filePath),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .patientAbhaId(req.patientAbhaId)
                .recordType(req.recordType)
                .diagnosis(req.diagnosis)
                .prescription(req.prescription)
                .notes(req.notes)
                .reportFileName(fileName)
                .reportFilePath(filePath)
                .build();

        record = recordRepository.save(record);
        return toResponse(record);
    }

    // ── Get by ABHA ID ────────────────────────────────────────────────────────

    public List<MedicalRecordResponse> getRecordsByAbhaId(String abhaId) {
        return recordRepository
                .findByPatientAbhaIdOrderByUploadDateDesc(abhaId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get for Patient ───────────────────────────────────────────────────────

    public List<MedicalRecordResponse> getRecordsForPatient(Long patientId) {
        return recordRepository
                .findByPatient_PatientIdOrderByUploadDateDesc(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get by Doctor ─────────────────────────────────────────────────────────

    public List<MedicalRecordResponse> getRecordsByDoctor(Long doctorId) {
        return recordRepository
                .findByDoctor_DoctorIdOrderByUploadDateDesc(doctorId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Counts ────────────────────────────────────────────────────────────────

    public long countForPatient(Long patientId) {
        return recordRepository.countByPatient_PatientId(patientId);
    }

    public long countForDoctor(Long doctorId) {
        return recordRepository.countByDoctor_DoctorId(doctorId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null) throw new BadRequestException("Invalid file.");
        String lower = name.toLowerCase();
        if (!lower.endsWith(".pdf") && !lower.endsWith(".jpg")
                && !lower.endsWith(".jpeg") && !lower.endsWith(".png")) {
            throw new BadRequestException("Only PDF, JPG, and PNG files are allowed.");
        }
        if (file.getSize() > 10 * 1024 * 1024)
            throw new BadRequestException("File size must not exceed 10MB.");
    }

    private MedicalRecordResponse toResponse(MedicalRecord r) {
        String downloadUrl = r.getReportFileName() != null
                ? "/uploads/" + r.getReportFileName() : null;

        return MedicalRecordResponse.builder()
                .recordId(r.getRecordId())
                .patientName(r.getPatient().getName())
                .patientAbhaId(r.getPatientAbhaId())
                .doctorName(r.getDoctor().getName())
                .hospital(r.getDoctor().getHospital())
                .recordType(r.getRecordType().name())
                .diagnosis(r.getDiagnosis())
                .prescription(r.getPrescription())
                .notes(r.getNotes())
                .reportFileName(r.getReportFileName())
                .downloadUrl(downloadUrl)
                .uploadDate(r.getUploadDate())
                .build();
    }
}
