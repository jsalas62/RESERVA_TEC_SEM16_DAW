package com.reservatec.backendreservatec.controlador;

import com.reservatec.backendreservatec.modelo.Usuario;
import com.reservatec.backendreservatec.servicio.CarreraService;
import com.reservatec.backendreservatec.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CarreraService carreraService;

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/user/form")
    public String getUserForm(OAuth2AuthenticationToken token, Model model, Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return "redirect:/login";
        }

        Usuario usuario = ensureUser(token);
        if (usuario == null) {
            usuario = createUserFromToken(token);
            model.addAttribute("usuario", usuario);
            model.addAttribute("carreras", carreraService.findAllCarreras());
            return "userForm";
        }

        return "redirect:/home";
    }

    @GetMapping("/user/profile")
    public String getProfileForm(OAuth2AuthenticationToken token, Model model, Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return "redirect:/login";
        }

        Usuario usuario = ensureUser(token);
        if (usuario == null) {
            return "redirect:/user/form";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("carreras", carreraService.findAllCarreras());
        return "profile";
    }

    @PostMapping("/user/form")
    public String submitUserForm(@ModelAttribute Usuario usuario) {
        usuarioService.saveUsuario(usuario);
        return "redirect:/success";
    }

    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

    @PostMapping("/user/profile")
    public String updateProfile(@ModelAttribute Usuario usuario) {
        usuarioService.updateUsuario(usuario);
        return "redirect:/user/profile?success";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    private Usuario ensureUser(OAuth2AuthenticationToken token) {
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");
        return usuarioService.findByEmail(email);
    }

    private Usuario createUserFromToken(OAuth2AuthenticationToken token) {
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        Usuario usuario = new Usuario();
        usuario.setEmail((String) attributes.get("email"));
        usuario.setNombres((String) attributes.get("name"));
        return usuario;
    }
}
