package com.reservatec.backendreservatec.controlador;

import com.reservatec.backendreservatec.modelo.Estado;
import com.reservatec.backendreservatec.servicio.EstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estados")
public class EstadoController {

    @Autowired
    private EstadoService estadoService;

    @GetMapping
    public List<Estado> getAllEstados() {
        return estadoService.findAllEstados();
    }

    @PostMapping
    public Estado createEstado(@RequestBody Estado estado) {
        return estadoService.saveEstado(estado);
    }
}
