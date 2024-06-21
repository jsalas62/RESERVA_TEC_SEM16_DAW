package com.reservatec.backendreservatec.webs;

import com.reservatec.backendreservatec.domains.UsuarioTO;
import com.reservatec.backendreservatec.entities.Usuario;
import com.reservatec.backendreservatec.services.AuthenticationService;
import com.reservatec.backendreservatec.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UsuarioController {

    private final AuthenticationService authenticationService;
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(AuthenticationService authenticationService, UsuarioService usuarioService) {
        this.authenticationService = authenticationService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/home")
    public ResponseEntity<String> homePage(Authentication authentication) {
        if (!authenticationService.isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No estás autorizado para acceder a esta página.");
        }
        return ResponseEntity.ok("Welcome to the Home Page");
    }

    @GetMapping("/login")
    public ResponseEntity<Void> loginRedirect(OAuth2AuthenticationToken token, Authentication authentication) {
        if (!authenticationService.isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Obtener el correo electrónico del usuario desde el token
        String email = token.getPrincipal().getAttribute("email");
        boolean usuarioRegistrado = usuarioService.existsByEmail(email);

        if (usuarioRegistrado) {
            // Redirigir a /home si el usuario ya está registrado
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/api/user/home").build();
        } else {
            // Redirigir a /register si el usuario no está registrado
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/api/user/register").build();
        }
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
        try {
            Usuario usuario = authenticationService.convertToEntity(usuarioTO);
            usuarioService.saveUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<UsuarioTO> updateProfile(@RequestBody UsuarioTO usuarioTO) {
        usuarioService.updateUsuario(authenticationService.convertToEntity(usuarioTO));
        return ResponseEntity.ok(usuarioTO);
    }
}
