package com.reservatec.backendreservatec.webs;

import com.reservatec.backendreservatec.domains.UsuarioTO;
import com.reservatec.backendreservatec.entities.Carrera;
import com.reservatec.backendreservatec.entities.Usuario;
import com.reservatec.backendreservatec.services.AuthenticationService;
import com.reservatec.backendreservatec.services.CarreraService;
import com.reservatec.backendreservatec.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Usamos RestController para que devuelva JSON
@RequestMapping("/api/user")
public class UsuarioController {

    private final AuthenticationService authenticationService;
    private final CarreraService carreraService;
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(AuthenticationService authenticationService,
                             CarreraService carreraService,
                             UsuarioService usuarioService) {
        this.authenticationService = authenticationService;
        this.carreraService = carreraService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/home")
    public ResponseEntity<String> homePage(Authentication authentication) {
        if (!authenticationService.isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No estás autorizado para acceder a esta página.");
        }
        return ResponseEntity.ok("Welcome to the Home Page");
    }

    @GetMapping("/form")
    public ResponseEntity<UsuarioTO> getUserForm(OAuth2AuthenticationToken token, Authentication authentication) {
        if (!authenticationService.isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UsuarioTO> usuarioTO = authenticationService.getUserFromToken(token);
        if (usuarioTO.isEmpty()) {
            UsuarioTO newUsuarioTO = authenticationService.createUserFromToken(token);
            return ResponseEntity.ok(newUsuarioTO);
        }

        return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/api/user/home").build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UsuarioTO> getProfileForm(OAuth2AuthenticationToken token, Authentication authentication) {
        if (!authenticationService.isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UsuarioTO> usuarioTO = authenticationService.getUserFromToken(token);
        if (usuarioTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/api/user/form").build();
        }

        return ResponseEntity.ok(usuarioTO.get());
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioTO> submitUserForm(@RequestBody UsuarioTO usuarioTO) {
        // Log para depuración
        System.out.println("Datos recibidos del usuario: " + usuarioTO);

        try {
            // Convertir DTO a entidad y guardar
            Usuario usuario = authenticationService.convertToEntity(usuarioTO);
            usuarioService.saveUsuario(usuario);

            // Log para depuración
            System.out.println("Usuario guardado con éxito: " + usuario);

            // Devolver respuesta con estado 201 (CREATED)
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioTO);
        } catch (Exception e) {
            // Manejar cualquier error que ocurra durante el proceso
            System.err.println("Error al guardar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<UsuarioTO> updateProfile(@RequestBody UsuarioTO usuarioTO) {
        usuarioService.updateUsuario(authenticationService.convertToEntity(usuarioTO));
        return ResponseEntity.ok(usuarioTO);
    }


    // Nuevo endpoint para obtener la lista de carreras
    @GetMapping("/carreras")
    public ResponseEntity<List<Carrera>> getCarreras() {
        List<Carrera> carreras = carreraService.findAllCarreras();
        return ResponseEntity.ok(carreras);
    }
}
