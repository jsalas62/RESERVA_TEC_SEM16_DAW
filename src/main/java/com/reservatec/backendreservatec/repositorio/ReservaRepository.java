package com.reservatec.backendreservatec.repositorio;

import com.reservatec.backendreservatec.modelo.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
