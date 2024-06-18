package com.reservatec.backendreservatec.webs;

import com.reservatec.backendreservatec.domains.UsuarioTO;
import com.reservatec.backendreservatec.entities.Usuario;
import com.reservatec.backendreservatec.mappers.UsuarioMapper;
import com.reservatec.backendreservatec.services.CarreraService;
import com.reservatec.backendreservatec.services.UsuarioService;
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

    private final UsuarioService usuarioService;
    private final CarreraService carreraService;
    private final UsuarioMapper usuarioMapper;

    // Constructor con inyección de dependencias
    public UsuarioController(UsuarioService usuarioService,
                             CarreraService carreraService,
                             UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.carreraService = carreraService;
        this.usuarioMapper = usuarioMapper;
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/user/form")
    public String getUserForm(OAuth2AuthenticationToken token, Model model, Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return "redirect:/login";
        }

        UsuarioTO usuarioTO = ensureUser(token);
        if (usuarioTO == null) {
            usuarioTO = createUserFromToken(token);
            model.addAttribute("usuario", usuarioTO);
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

        UsuarioTO usuarioTO = ensureUser(token);
        if (usuarioTO == null) {
            return "redirect:/user/form";
        }

        model.addAttribute("usuario", usuarioTO);
        model.addAttribute("carreras", carreraService.findAllCarreras());
        return "profile";
    }

    @PostMapping("/user/form")
    public String submitUserForm(@ModelAttribute UsuarioTO usuarioTO) {
        Usuario usuario = usuarioMapper.toEntity(usuarioTO);
        usuarioService.saveUsuario(usuario);
        return "redirect:/success";
    }

    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

    @PostMapping("/user/profile")
    public String updateProfile(@ModelAttribute UsuarioTO usuarioTO) {
        Usuario usuario = usuarioMapper.toEntity(usuarioTO);
        usuarioService.updateUsuario(usuario);
        return "redirect:/user/profile?success";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    private UsuarioTO ensureUser(OAuth2AuthenticationToken token) {
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");
        return usuarioService.findByEmail(email)
                .map(usuarioMapper::toTO)
                .orElse(null);
    }

    private UsuarioTO createUserFromToken(OAuth2AuthenticationToken token) {
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        UsuarioTO usuarioTO = new UsuarioTO();
        usuarioTO.setEmail((String) attributes.get("email"));
        usuarioTO.setNombres((String) attributes.get("name"));
        return usuarioTO;
    }
}
