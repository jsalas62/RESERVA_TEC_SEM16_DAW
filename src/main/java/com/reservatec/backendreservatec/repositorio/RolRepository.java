package com.reservatec.backendreservatec.repositorio;
import com.reservatec.backendreservatec.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
}
