package com.reservatec.backendreservatec.controlador;

import com.reservatec.backendreservatec.modelo.Reserva;
import com.reservatec.backendreservatec.modelo.Usuario;
import com.reservatec.backendreservatec.servicio.CampoService;
import com.reservatec.backendreservatec.servicio.HorarioService;
import com.reservatec.backendreservatec.servicio.ReservaService;
import com.reservatec.backendreservatec.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reserva")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CampoService campoService;

    @Autowired
    private HorarioService horarioService;

    @GetMapping("/nueva")
    public String getReservaForm(OAuth2AuthenticationToken token, Model model, Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return "redirect:/login";
        }

        Usuario usuario = ensureUser(token);
        if (usuario == null) {
            return "redirect:/user/form";
        }

        model.addAttribute("reserva", new Reserva());
        model.addAttribute("campos", campoService.findAllCampos());
        model.addAttribute("horarios", horarioService.findAllHorarios());
        return "reservaForm";
    }

    @PostMapping("/nueva")
    public String submitReservaForm(@ModelAttribute Reserva reserva, OAuth2AuthenticationToken token, Model model) {
        Usuario usuario = ensureUser(token);
        if (usuario == null) {
            return "redirect:/user/form";
        }

        reserva.setUsuario(usuario);

        try {
            reservaService.crearReserva(reserva);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("campos", campoService.findAllCampos());
            model.addAttribute("horarios", horarioService.findAllHorarios());
            return "reservaForm";
        }

        return "redirect:/reserva/success";
    }

    @GetMapping("/misreservas")
    public String getMisReservas(OAuth2AuthenticationToken token, Model model, Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return "redirect:/login";
        }

        Usuario usuario = ensureUser(token);
        if (usuario == null) {
            return "redirect:/user/form";
        }

        List<Reserva> reservas = reservaService.findReservasByUsuario(usuario.getId());
        model.addAttribute("reservas", reservas);
        return "misReservas";
    }

    @GetMapping("/success")
    public String successPage() {
        return "successReserva";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    private Usuario ensureUser(OAuth2AuthenticationToken token) {
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");
        return usuarioService.findByEmail(email);
    }
}
