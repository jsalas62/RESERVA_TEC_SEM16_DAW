package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.*;
import com.reservatec.backendreservatec.repositorio.CampoRepository;
import com.reservatec.backendreservatec.repositorio.EstadoRepository;
import com.reservatec.backendreservatec.repositorio.HorarioRepository;
import com.reservatec.backendreservatec.repositorio.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private CampoRepository campoRepository;

    @Mock
    private HorarioRepository horarioRepository;

    @InjectMocks
    private ReservaService reservaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
    }

    @Test
    public void testCrearReserva_FechaPasada() {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now().minusDays(1));

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReserva(reserva);
        });

        assertEquals("No se puede reservar en fechas pasadas.", exception.getMessage());
    }

    @Test
    public void testCrearReserva_CampoNoEncontrado() {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now().plusDays(1));
        Campo campo = new Campo();
        campo.setId(1L);
        reserva.setCampo(campo);

        when(campoRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReserva(reserva);
        });

        assertEquals("Campo no encontrado", exception.getMessage());
    }

    @Test
    public void testCrearReserva_HorarioNoEncontrado() {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now().plusDays(1));
        Campo campo = new Campo();
        campo.setId(1L);
        Horario horario = new Horario();
        horario.setId(1L);
        reserva.setCampo(campo);
        reserva.setHorario(horario);

        when(campoRepository.findById(1L)).thenReturn(Optional.of(campo));
        when(horarioRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReserva(reserva);
        });

        assertEquals("Horario no encontrado", exception.getMessage());
    }

    @Test
    public void testCrearReserva_HoraPasadaHoy() {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now());
        Campo campo = new Campo();
        campo.setId(1L);
        Horario horario = new Horario();
        horario.setId(1L);
        horario.setHoraInicio(LocalTime.now().minusHours(1));
        reserva.setCampo(campo);
        reserva.setHorario(horario);

        when(campoRepository.findById(1L)).thenReturn(Optional.of(campo));
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReserva(reserva);
        });

        assertEquals("No se puede reservar en horas pasadas si la reserva es para hoy.", exception.getMessage());
    }

    @Test
    public void testCrearReserva_UsuarioYaTieneReserva() {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now().plusDays(1));
        Campo campo = new Campo();
        campo.setId(1L);
        Horario horario = new Horario();
        horario.setId(1L);
        reserva.setCampo(campo);
        reserva.setHorario(horario);
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        reserva.setUsuario(usuario);

        when(campoRepository.findById(1L)).thenReturn(Optional.of(campo));
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(reservaRepository.findByUsuarioIdAndFecha(1L, reserva.getFecha())).thenReturn(Optional.of(new Reserva()));

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReserva(reserva);
        });

        assertEquals("El usuario ya tiene una reserva para esta fecha.", exception.getMessage());
    }

    @Test
    public void testCrearReserva_CampoReservado() {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now().plusDays(1));
        Campo campo = new Campo();
        campo.setId(1L);
        Horario horario = new Horario();
        horario.setId(1L);
        reserva.setCampo(campo);
        reserva.setHorario(horario);
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        reserva.setUsuario(usuario);

        when(campoRepository.findById(1L)).thenReturn(Optional.of(campo));
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(reservaRepository.findByCampoIdAndHorarioIdAndFecha(1L, 1L, reserva.getFecha())).thenReturn(Optional.of(new Reserva()));

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReserva(reserva);
        });

        assertEquals("El campo ya está reservado en este horario para esta fecha.", exception.getMessage());
    }

    @Test
    public void testCrearReserva_ReservaSemanaSiguiente() {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now().plusDays(1));
        Campo campo = new Campo();
        campo.setId(1L);
        Horario horario = new Horario();
        horario.setId(1L);
        reserva.setCampo(campo);
        reserva.setHorario(horario);
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        reserva.setUsuario(usuario);

        Reserva ultimaReservaCompletada = new Reserva();
        ultimaReservaCompletada.setFecha(LocalDate.now().minusDays(2));

        when(campoRepository.findById(1L)).thenReturn(Optional.of(campo));
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(reservaRepository.findByUsuarioIdAndFecha(1L, reserva.getFecha())).thenReturn(Optional.empty());
        when(reservaRepository.findByCampoIdAndHorarioIdAndFecha(1L, 1L, reserva.getFecha())).thenReturn(Optional.empty());
        when(reservaRepository.findByUsuarioIdAndEstadoNombre(1L, "Completada")).thenReturn(Collections.singletonList(ultimaReservaCompletada));

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReserva(reserva);
        });

        assertEquals("El usuario solo puede realizar una nueva reserva una semana después de la última reserva completada.", exception.getMessage());
    }

    @Test
    public void testCrearReserva_ReservaActiva() {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now().plusDays(1));
        Campo campo = new Campo();
        campo.setId(1L);
        Horario horario = new Horario();
        horario.setId(1L);
        reserva.setCampo(campo);
        reserva.setHorario(horario);
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        reserva.setUsuario(usuario);

        when(campoRepository.findById(1L)).thenReturn(Optional.of(campo));
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(reservaRepository.findByUsuarioIdAndFecha(1L, reserva.getFecha())).thenReturn(Optional.empty());
        when(reservaRepository.findByCampoIdAndHorarioIdAndFecha(1L, 1L, reserva.getFecha())).thenReturn(Optional.empty());
        when(reservaRepository.findByUsuarioIdAndEstadoNombre(1L, "Completada")).thenReturn(Collections.emptyList());
        when(reservaRepository.findByUsuarioIdAndEstadoNombre(1L, "Activo")).thenReturn(Collections.singletonList(new Reserva()));

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.crearReserva(reserva);
        });

        assertEquals("El usuario no puede realizar una nueva reserva mientras tenga una reserva activa.", exception.getMessage());
    }

    @Test
    public void testCrearReserva_Exitoso() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now().plusDays(1));
        Campo campo = new Campo();
        campo.setId(1L);
        Horario horario = new Horario();
        horario.setId(1L);
        reserva.setCampo(campo);
        reserva.setHorario(horario);
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        reserva.setUsuario(usuario);

        Estado estadoActivo = new Estado();
        estadoActivo.setId(1L);
        estadoActivo.setNombre("Activo");

        when(campoRepository.findById(1L)).thenReturn(Optional.of(campo));
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(reservaRepository.findByUsuarioIdAndFecha(1L, reserva.getFecha())).thenReturn(Optional.empty());
        when(reservaRepository.findByCampoIdAndHorarioIdAndFecha(1L, 1L, reserva.getFecha())).thenReturn(Optional.empty());
        when(reservaRepository.findByUsuarioIdAndEstadoNombre(1L, "Completada")).thenReturn(Collections.emptyList());
        when(reservaRepository.findByUsuarioIdAndEstadoNombre(1L, "Activo")).thenReturn(Collections.emptyList());
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estadoActivo));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        Reserva resultado = reservaService.crearReserva(reserva);

        assertNotNull(resultado);
        assertEquals(estadoActivo, resultado.getEstado());
    }

    @Test
    public void testFindReservasByUsuario() {
        Long usuarioId = 1L;
        Reserva reserva = new Reserva();
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        reserva.setUsuario(usuario);

        when(reservaRepository.findByUsuarioId(usuarioId)).thenReturn(Collections.singletonList(reserva));

        List<Reserva> reservas = reservaService.findReservasByUsuario(usuarioId);

        assertNotNull(reservas);
        assertEquals(1, reservas.size());
        assertEquals(usuarioId, reservas.get(0).getUsuario().getId());
    }

    @Test
    public void testCompletarReserva_ReservaNoEncontrada() {
        Long reservaId = 1L;
        when(reservaRepository.findById(reservaId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.completarReserva(reservaId);
        });

        assertEquals("Reserva no encontrada", exception.getMessage());
    }

    @Test
    public void testCompletarReserva_EstadoCompletadoNoEncontrado() {
        Long reservaId = 1L;
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now());
        Horario horario = new Horario();
        horario.setHoraFin(LocalTime.now().minusMinutes(1));
        reserva.setHorario(horario);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));
        when(estadoRepository.findById(3L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.completarReserva(reservaId);
        });

        assertEquals("Estado completado no encontrado", exception.getMessage());
    }

    @Test
    public void testCompletarReserva_AntesDeFinalizacion() {
        Long reservaId = 1L;
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now());
        Horario horario = new Horario();
        horario.setHoraFin(LocalTime.now().plusMinutes(1));
        reserva.setHorario(horario);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.completarReserva(reservaId);
        });

        assertEquals("La reserva no puede completarse antes de su finalización.", exception.getMessage());
    }

    @Test
    public void testCompletarReserva_Exitoso() throws Exception {
        Long reservaId = 1L;
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now());
        Horario horario = new Horario();
        horario.setHoraFin(LocalTime.now().minusMinutes(1));
        reserva.setHorario(horario);

        Estado estadoCompletada = new Estado();
        estadoCompletada.setId(3L);
        estadoCompletada.setNombre("Completada");

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));
        when(estadoRepository.findById(3L)).thenReturn(Optional.of(estadoCompletada));

        reservaService.completarReserva(reservaId);

        verify(reservaRepository, times(1)).save(reserva);
        assertEquals(estadoCompletada, reserva.getEstado());
    }

    @Test
    public void testActualizarReservas() {
        Reserva reserva1 = new Reserva();
        reserva1.setId(1L);
        reserva1.setFecha(LocalDate.now().minusDays(1));
        Horario horario1 = new Horario();
        horario1.setHoraFin(LocalTime.now().minusHours(2));
        reserva1.setHorario(horario1);
        Estado estadoActivo = new Estado();
        estadoActivo.setId(1L);
        estadoActivo.setNombre("Activo");
        reserva1.setEstado(estadoActivo);

        Reserva reserva2 = new Reserva();
        reserva2.setId(2L);
        reserva2.setFecha(LocalDate.now());
        Horario horario2 = new Horario();
        horario2.setHoraFin(LocalTime.now().minusHours(1));
        reserva2.setHorario(horario2);
        reserva2.setEstado(estadoActivo);

        List<Reserva> reservasActivas = List.of(reserva1, reserva2);

        Estado estadoCompletada = new Estado();
        estadoCompletada.setId(3L);
        estadoCompletada.setNombre("Completada");

        when(reservaRepository.findByEstadoNombre("Activo")).thenReturn(reservasActivas);
        when(estadoRepository.findById(3L)).thenReturn(Optional.of(estadoCompletada));

        reservaService.actualizarReservas();

        verify(reservaRepository, times(1)).save(reserva1);
        verify(reservaRepository, times(1)).save(reserva2);
        assertEquals(estadoCompletada, reserva1.getEstado());
        assertEquals(estadoCompletada, reserva2.getEstado());
    }
}
