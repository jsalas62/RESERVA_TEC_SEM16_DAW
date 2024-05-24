package com.reservatec.backendreservatec.repositorio;

import com.reservatec.backendreservatec.modelo.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
}
