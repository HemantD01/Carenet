package com.carenet.dto;

public class DoctorResponse {
    public Long    doctorId;
    public String  name;
    public String  specialization;
    public String  hospital;
    public String  location;
    public Integer experienceYears;
    public Double  rating;
    public Integer totalRatings;
    public Integer consultationFee;
    public String  bio;
    public boolean available;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final DoctorResponse r = new DoctorResponse();
        public Builder doctorId(Long v)          { r.doctorId = v; return this; }
        public Builder name(String v)            { r.name = v; return this; }
        public Builder specialization(String v)  { r.specialization = v; return this; }
        public Builder hospital(String v)        { r.hospital = v; return this; }
        public Builder location(String v)        { r.location = v; return this; }
        public Builder experienceYears(Integer v){ r.experienceYears = v; return this; }
        public Builder rating(Double v)          { r.rating = v; return this; }
        public Builder totalRatings(Integer v)   { r.totalRatings = v; return this; }
        public Builder consultationFee(Integer v){ r.consultationFee = v; return this; }
        public Builder bio(String v)             { r.bio = v; return this; }
        public Builder available(boolean v)      { r.available = v; return this; }
        public DoctorResponse build()            { return r; }
    }
}
