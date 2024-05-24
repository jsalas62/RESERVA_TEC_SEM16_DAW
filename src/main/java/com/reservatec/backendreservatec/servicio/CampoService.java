package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.Campo;
import com.reservatec.backendreservatec.repositorio.CampoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampoService {

    @Autowired
    private CampoRepository campoRepository;

    public List<Campo> findAllCampos() {
        return campoRepository.findAll();
    }

    public Campo saveCampo(Campo campo) {
        return campoRepository.save(campo);
    }
}
