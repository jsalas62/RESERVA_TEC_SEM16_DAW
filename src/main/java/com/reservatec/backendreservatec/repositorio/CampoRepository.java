package com.reservatec.backendreservatec.repositorio;

import com.reservatec.backendreservatec.modelo.Campo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampoRepository extends JpaRepository<Campo, Long> {
}
