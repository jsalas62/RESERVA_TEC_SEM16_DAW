package com.reservatec.backendreservatec.repositorio;

import com.reservatec.backendreservatec.modelo.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
}
