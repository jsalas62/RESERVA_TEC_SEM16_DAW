package com.reservatec.backendreservatec.repositorio;

import com.reservatec.backendreservatec.modelo.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {
}
