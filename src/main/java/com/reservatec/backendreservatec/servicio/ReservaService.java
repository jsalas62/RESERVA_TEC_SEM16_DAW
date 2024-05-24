package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.Reserva;
import com.reservatec.backendreservatec.repositorio.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public List<Reserva> findAllReservas() {
        return reservaRepository.findAll();
    }

    public Reserva saveReserva(Reserva reserva) {
        return reservaRepository.save(reserva);
    }
}
