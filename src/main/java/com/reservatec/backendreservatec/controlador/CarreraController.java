package com.reservatec.backendreservatec.controlador;

import com.reservatec.backendreservatec.modelo.Carrera;
import com.reservatec.backendreservatec.servicio.CarreraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carreras")
public class CarreraController {

    @Autowired
    private CarreraService carreraService;

    @GetMapping
    public List<Carrera> getAllCarreras() {
        return carreraService.findAllCarreras();
    }

}
