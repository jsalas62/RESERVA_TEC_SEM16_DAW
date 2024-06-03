package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.Estado;
import com.reservatec.backendreservatec.repositorio.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstadoService {

    @Autowired
    private EstadoRepository estadoRepository;

    public Estado findById(Long id) {
        return estadoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
    }
}
