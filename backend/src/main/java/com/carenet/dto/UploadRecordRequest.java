package com.carenet.dto;

import com.carenet.model.MedicalRecord;

public class UploadRecordRequest {
    public String                   patientAbhaId;
    public MedicalRecord.RecordType recordType;
    public String                   diagnosis;
    public String                   prescription;
    public String                   notes;
}
