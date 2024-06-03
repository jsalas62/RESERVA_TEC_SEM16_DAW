package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.Campo;
import com.reservatec.backendreservatec.modelo.Estado;
import com.reservatec.backendreservatec.modelo.Horario;
import com.reservatec.backendreservatec.modelo.Reserva;
import com.reservatec.backendreservatec.repositorio.CampoRepository;
import com.reservatec.backendreservatec.repositorio.EstadoRepository;
import com.reservatec.backendreservatec.repositorio.HorarioRepository;
import com.reservatec.backendreservatec.repositorio.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private CampoRepository campoRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    public ReservaService() {
        // Establecer zona horaria por defecto a America/Lima
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
    }

    public Reserva crearReserva(Reserva reserva) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        // Validar que la fecha de reserva es hoy o en el futuro
        if (reserva.getFecha().isBefore(today)) {
            throw new Exception("No se puede reservar en fechas pasadas.");
        }

        // Obtener campo y horario desde el repositorio para asegurar que existen
        Campo campo = campoRepository.findById(reserva.getCampo().getId())
                .orElseThrow(() -> new Exception("Campo no encontrado"));
        Horario horario = horarioRepository.findById(reserva.getHorario().getId())
                .orElseThrow(() -> new Exception("Horario no encontrado"));

        // Validar que la hora de reserva es en el futuro solo si la fecha es hoy
        if (reserva.getFecha().isEqual(today)) {
            if (horario.getHoraInicio() == null || horario.getHoraInicio().isBefore(currentTime)) {
                throw new Exception("No se puede reservar en horas pasadas si la reserva es para hoy.");
            }
        }

        // Validar que el usuario no tiene otra reserva en la misma fecha
        Optional<Reserva> reservaExistente = reservaRepository.findByUsuarioIdAndFecha(reserva.getUsuario().getId(), reserva.getFecha());
        if (reservaExistente.isPresent()) {
            throw new Exception("El usuario ya tiene una reserva para esta fecha.");
        }

        // Validar que el campo no está reservado en el mismo horario y fecha
        Optional<Reserva> reservaCampoExistente = reservaRepository.findByCampoIdAndHorarioIdAndFecha(campo.getId(), horario.getId(), reserva.getFecha());
        if (reservaCampoExistente.isPresent()) {
            throw new Exception("El campo ya está reservado en este horario para esta fecha.");
        }

        // Validar que el usuario puede hacer una nueva reserva solo una semana después de la última reserva completada
        List<Reserva> reservasCompletadas = reservaRepository.findByUsuarioIdAndEstadoNombre(reserva.getUsuario().getId(), "Completada");
        if (!reservasCompletadas.isEmpty()) {
            Reserva ultimaReserva = reservasCompletadas.get(0); // Asumiendo que está ordenado por fecha descendente
            LocalDate fechaUltimaReserva = ultimaReserva.getFecha();
            if (reserva.getFecha().isBefore(fechaUltimaReserva.plusWeeks(1))) {
                throw new Exception("El usuario solo puede realizar una nueva reserva una semana después de la última reserva completada.");
            }
        }

        // Asignar estado por defecto
        Estado estadoActivo = estadoRepository.findById(1L).orElseThrow(() -> new Exception("Estado activo no encontrado"));
        reserva.setEstado(estadoActivo);

        return reservaRepository.save(reserva);
    }

    public List<Reserva> findReservasByUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
}
