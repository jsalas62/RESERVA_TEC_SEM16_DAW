package com.reservatec.backendreservatec.repositorio;

import com.reservatec.backendreservatec.modelo.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {


}
