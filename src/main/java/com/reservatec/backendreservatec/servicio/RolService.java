package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.Rol;
import com.reservatec.backendreservatec.repositorio.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> findAllRoles() {
        return rolRepository.findAll();
    }

    public Rol saveRol(Rol rol) {
        return rolRepository.save(rol);
    }
}