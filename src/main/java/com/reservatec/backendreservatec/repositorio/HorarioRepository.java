package com.reservatec.backendreservatec.repositorio;

import com.reservatec.backendreservatec.modelo.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
}
