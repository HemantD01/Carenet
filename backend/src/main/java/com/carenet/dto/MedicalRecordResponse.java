package com.carenet.dto;

import java.time.LocalDateTime;

public class MedicalRecordResponse {
    public Long          recordId;
    public String        patientName;
    public String        patientAbhaId;
    public String        doctorName;
    public String        hospital;
    public String        recordType;
    public String        diagnosis;
    public String        prescription;
    public String        notes;
    public String        reportFileName;
    public String        downloadUrl;
    public LocalDateTime uploadDate;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final MedicalRecordResponse r = new MedicalRecordResponse();
        public Builder recordId(Long v)           { r.recordId = v; return this; }
        public Builder patientName(String v)      { r.patientName = v; return this; }
        public Builder patientAbhaId(String v)    { r.patientAbhaId = v; return this; }
        public Builder doctorName(String v)       { r.doctorName = v; return this; }
        public Builder hospital(String v)         { r.hospital = v; return this; }
        public Builder recordType(String v)       { r.recordType = v; return this; }
        public Builder diagnosis(String v)        { r.diagnosis = v; return this; }
        public Builder prescription(String v)     { r.prescription = v; return this; }
        public Builder notes(String v)            { r.notes = v; return this; }
        public Builder reportFileName(String v)   { r.reportFileName = v; return this; }
        public Builder downloadUrl(String v)      { r.downloadUrl = v; return this; }
        public Builder uploadDate(LocalDateTime v) { r.uploadDate = v; return this; }
        public MedicalRecordResponse build()      { return r; }
    }
}
