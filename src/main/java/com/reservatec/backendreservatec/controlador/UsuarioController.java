package com.reservatec.backendreservatec.controlador;

import com.reservatec.backendreservatec.modelo.Usuario;
import com.reservatec.backendreservatec.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/user/form")
    public String getUserForm(OAuth2AuthenticationToken token, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";  // Redirigir a la página de inicio de sesión si no está autenticado
        }

        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");

        // Verificar si el usuario ya existe en la base de datos
        Usuario existingUser = usuarioService.findByEmail(email);
        if (existingUser != null) {
            return "redirect:/user/alreadyExists";  // Redirigir a una página que indica que el usuario ya existe
        }

        String name = (String) attributes.get("name");

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setNombres(name);

        model.addAttribute("usuario", usuario);
        return "userForm";  // Esta es la vista del formulario HTML
    }

    @PostMapping("/user/form")
    public String submitUserForm(@ModelAttribute Usuario usuario) {
        usuarioService.saveUsuario(usuario);
        return "redirect:/success";  // Redirige a la página de éxito después de guardar
    }

    @GetMapping("/success")
    public String successPage() {
        return "success";  // Nombre de la vista success.html
    }

    @GetMapping("/user/alreadyExists")
    public String userAlreadyExistsPage() {
        return "userAlreadyExists";  // Nombre de la vista userAlreadyExists.html
    }

    @GetMapping("/user/profile")
    public String getProfileForm(OAuth2AuthenticationToken token, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";  // Redirigir a la página de inicio de sesión si no está autenticado
        }

        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");

        // Obtener el usuario de la base de datos
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            return "redirect:/user/form";  // Redirigir al formulario de registro si el usuario no existe
        }

        model.addAttribute("usuario", usuario);
        return "profile";  // Esta es la vista del perfil HTML
    }

    @PostMapping("/user/profile")
    public String updateProfile(@ModelAttribute Usuario usuario) {
        usuarioService.updateUsuario(usuario);
        return "redirect:/user/profile?success";  // Redirige a la página de perfil después de actualizar
    }
}
