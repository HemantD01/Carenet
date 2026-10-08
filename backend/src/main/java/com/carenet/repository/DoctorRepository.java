package com.carenet.repository;

import com.carenet.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    Optional<Doctor> findByDoctorCode(String doctorCode);
    Optional<Doctor> findByEmailAndDoctorCode(String email, String doctorCode);

    @Query("SELECT d FROM Doctor d WHERE " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(d.hospital) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Doctor> searchDoctors(@Param("q") String query);

    boolean existsByEmail(String email);
    boolean existsByDoctorCode(String doctorCode);
}
