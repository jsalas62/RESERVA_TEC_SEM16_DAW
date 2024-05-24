package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.Estado;
import com.reservatec.backendreservatec.repositorio.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoService {

    @Autowired
    private EstadoRepository estadoRepository;

    public List<Estado> findAllEstados() {
        return estadoRepository.findAll();
    }

    public Estado saveEstado(Estado estado) {
        return estadoRepository.save(estado);
    }
}