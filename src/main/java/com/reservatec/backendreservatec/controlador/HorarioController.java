package com.reservatec.backendreservatec.controlador;

import com.reservatec.backendreservatec.modelo.Horario;
import com.reservatec.backendreservatec.servicio.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horarios")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @GetMapping
    public List<Horario> getAllHorarios() {
        return horarioService.findAllHorarios();
    }

    @PostMapping
    public Horario createHorario(@RequestBody Horario horario) {
        return horarioService.saveHorario(horario);
    }
}
