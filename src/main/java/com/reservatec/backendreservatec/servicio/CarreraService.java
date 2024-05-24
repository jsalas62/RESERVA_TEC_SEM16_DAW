package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.Carrera;
import com.reservatec.backendreservatec.repositorio.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarreraService {

    @Autowired
    private CarreraRepository carreraRepository;

    public List<Carrera> findAllCarreras() {
        return carreraRepository.findAll();
    }
}
