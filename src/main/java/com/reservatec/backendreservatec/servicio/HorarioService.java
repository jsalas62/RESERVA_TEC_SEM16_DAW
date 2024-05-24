package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.Horario;
import com.reservatec.backendreservatec.repositorio.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class HorarioService {

    @Autowired
    private HorarioRepository horarioRepository;

    public List<Horario> findAllHorarios() {
        return horarioRepository.findAll();
    }

    public Horario saveHorario(Horario horario) {
        return horarioRepository.save(horario);
    }
}
