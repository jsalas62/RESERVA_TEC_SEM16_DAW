package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.Campo;
import com.reservatec.backendreservatec.modelo.Estado;
import com.reservatec.backendreservatec.modelo.Horario;
import com.reservatec.backendreservatec.modelo.Reserva;
import com.reservatec.backendreservatec.modelo.Usuario;
import com.reservatec.backendreservatec.repositorio.EstadoRepository;
import com.reservatec.backendreservatec.repositorio.ReservaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario usuario;
    private Estado estadoActivo;
    private Reserva reserva;
    private Campo campo;
    private Horario horario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        estadoActivo = new Estado();
        estadoActivo.setId(1L);
        estadoActivo.setNombre("Activo");

        campo = new Campo();
        campo.setId(1L);
        campo.setNombre("Campo de Fútbol");
        campo.setAforo(22);
        campo.setEstado(estadoActivo);

        horario = new Horario();
        horario.setId(1L);
        horario.setHoraInicio(LocalTime.of(10, 0));
        horario.setHoraFin(LocalTime.of(12, 0));

        reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setCampo(campo);
        reserva.setFecha(LocalDate.now().plusDays(8)); // Fecha de reserva una semana después
        reserva.setHorario(horario);
    }

    @AfterEach
    public void afterEach() {
        System.out.println("Test completado.");
    }

    @Test
    public void testCrearReservaExitosamente() throws Exception {
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estadoActivo));
        when(reservaRepository.findByUsuarioIdAndFecha(usuario.getId(), reserva.getFecha())).thenReturn(Optional.empty());
        when(reservaRepository.findByCampoIdAndHorarioIdAndFecha(any(), any(), any())).thenReturn(Optional.empty());
        when(reservaRepository.findByUsuarioIdAndEstadoNombre(usuario.getId(), "Completada")).thenReturn(Collections.emptyList());
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        Reserva result = reservaService.crearReserva(reserva);

        assertNotNull(result, "La reserva debe ser creada exitosamente.");
        assertEquals(reserva.getFecha(), result.getFecha(), "La fecha de la reserva debe coincidir.");
    }

    @Test
    public void testCrearReservaFallaPorFechaPasada() {
        reserva.setFecha(LocalDate.now().minusDays(1)); // Fecha en el pasado

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReserva(reserva);
        });

        String expectedMessage = "No se puede reservar en fechas pasadas.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "El mensaje de excepción debe indicar que no se puede reservar en fechas pasadas.");
    }

    @Test
    public void testCrearReservaFallaPorReservaExistenteEnFecha() {
        when(reservaRepository.findByUsuarioIdAndFecha(usuario.getId(), reserva.getFecha())).thenReturn(Optional.of(new Reserva()));

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReserva(reserva);
        });

        String expectedMessage = "El usuario ya tiene una reserva para esta fecha.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "El mensaje de excepción debe indicar que el usuario ya tiene una reserva para esta fecha.");
    }

    @Test
    public void testCrearReservaFallaPorReservaExistenteEnCampoYHorario() {
        when(reservaRepository.findByUsuarioIdAndFecha(usuario.getId(), reserva.getFecha())).thenReturn(Optional.empty());
        when(reservaRepository.findByCampoIdAndHorarioIdAndFecha(any(), any(), any())).thenReturn(Optional.of(new Reserva()));

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReserva(reserva);
        });

        String expectedMessage = "El campo ya está reservado en este horario para esta fecha.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "El mensaje de excepción debe indicar que el campo ya está reservado en este horario para esta fecha.");
    }

    @Test
    public void testCrearReservaFallaPorSemanaNoCumplida() {
        Reserva reservaCompletada = new Reserva();
        reservaCompletada.setFecha(LocalDate.now().minusDays(2)); // Fecha de la última reserva completada hace 2 días
        reservaCompletada.setEstado(estadoActivo);

        // Configurar las expectativas del repositorio
        when(reservaRepository.findByUsuarioIdAndFecha(usuario.getId(), reserva.getFecha())).thenReturn(Optional.empty());
        when(reservaRepository.findByCampoIdAndHorarioIdAndFecha(any(), any(), any())).thenReturn(Optional.empty());
        when(reservaRepository.findByUsuarioIdAndEstadoNombre(usuario.getId(), "Completada")).thenReturn(Collections.singletonList(reservaCompletada));

        // Ejecutar y verificar la excepción
        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReserva(reserva);
        });
    }
}
