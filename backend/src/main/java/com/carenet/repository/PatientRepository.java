package com.carenet.repository;

import com.carenet.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByAbhaId(String abhaId);
    Optional<Patient> findByPhone(String phone);
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByPhoneOrAbhaId(String phone, String abhaId);
    boolean existsByAbhaId(String abhaId);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
}
