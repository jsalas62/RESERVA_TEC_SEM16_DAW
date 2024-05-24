package com.reservatec.backendreservatec.controlador;

import com.reservatec.backendreservatec.modelo.Campo;
import com.reservatec.backendreservatec.servicio.CampoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campos")
public class CampoController {

    @Autowired
    private CampoService campoService;

    @GetMapping
    public List<Campo> getAllCampos() {
        return campoService.findAllCampos();
    }

    @PostMapping
    public Campo createCampo(@RequestBody Campo campo) {
        return campoService.saveCampo(campo);
    }
}
