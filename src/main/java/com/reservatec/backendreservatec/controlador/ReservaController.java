package com.reservatec.backendreservatec.controlador;

import com.reservatec.backendreservatec.modelo.Reserva;
import com.reservatec.backendreservatec.servicio.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public List<Reserva> getAllReservas() {
        return reservaService.findAllReservas();
    }

    @PostMapping
    public Reserva createReserva(@RequestBody Reserva reserva) {
        return reservaService.saveReserva(reserva);
    }
}
