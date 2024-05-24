package com.reservatec.backendreservatec.servicio;

import com.reservatec.backendreservatec.modelo.Estado;
import com.reservatec.backendreservatec.modelo.Rol;
import com.reservatec.backendreservatec.modelo.Usuario;
import com.reservatec.backendreservatec.repositorio.UsuarioRepository;
import com.reservatec.backendreservatec.repositorio.EstadoRepository;
import com.reservatec.backendreservatec.repositorio.RolRepository;
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
            usuarioRepository.save(existingUser);
        }
    }
}
