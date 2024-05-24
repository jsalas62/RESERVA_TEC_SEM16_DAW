package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.Alerta;
import com.reservatec.backendreservatec.repositorio.AlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertaService {

    @Autowired
    public AlertaRepository alertaRepository;

    public List<Alerta> findAllAlertas () {
        return alertaRepository.findAll();
    }

    public Alerta saveAlerta(Alerta alerta) {
        return alertaRepository.save(alerta);
    }
}
