package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.*;
import com.reservatec.backendreservatec.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EstadoRepository estadoRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private CarreraRepository carreraRepository; // Asegúrate de crear este repositorio

    public void saveUsuario(Usuario usuario) {
        Usuario existingUser = usuarioRepository.findByEmail(usuario.getEmail());
        if (existingUser == null) {
            if (usuario.getCodigoTecsup() == null) {
                throw new IllegalArgumentException("El código Tecsup no puede ser nulo.");
            }
            if (usuario.getEstado() == null) {
                usuario.setEstado(estadoRepository.findById(1L).orElse(null));
            }
            if (usuario.getRol() == null) {
                usuario.setRol(rolRepository.findById(2L).orElse(null));
            }
            if (usuario.getCarrera() != null && usuario.getCarrera().getId() != null) {
                Carrera carrera = carreraRepository.findById(usuario.getCarrera().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Carrera no encontrada con ID: " + usuario.getCarrera().getId()));
                usuario.setCarrera(carrera);
            }
            usuarioRepository.save(usuario);
        } else {
            if (usuario.getNombres() != null) {
                existingUser.setNombres(usuario.getNombres());
            }
            if (usuario.getCodigoTecsup() != null) {
                existingUser.setCodigoTecsup(usuario.getCodigoTecsup());
            }
            if (usuario.getEstado() != null) {
                existingUser.setEstado(usuario.getEstado());
            }
            if (usuario.getRol() != null) {
                existingUser.setRol(usuario.getRol());
            }
            if (usuario.getCarrera() != null && usuario.getCarrera().getId() != null) {
                Carrera carrera = carreraRepository.findById(usuario.getCarrera().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Carrera no encontrada con ID: " + usuario.getCarrera().getId()));
                existingUser.setCarrera(carrera);
            }
            usuarioRepository.save(existingUser);
        }
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public void updateUsuario(Usuario usuario) {
        Usuario existingUser = usuarioRepository.findByEmail(usuario.getEmail());
        if (existingUser != null) {
            existingUser.setCodigoTecsup(usuario.getCodigoTecsup());
            existingUser.setCarrera(usuario.getCarrera());
            usuarioRepository.save(existingUser);
        }
    }
}
