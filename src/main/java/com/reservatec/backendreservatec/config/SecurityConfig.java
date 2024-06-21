package com.reservatec.backendreservatec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Actualiza el URL del manejador de éxito de autenticación
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler("https://balanced-delight-production.up.railway.app/api/user/login");

        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configurar CORS
                .authorizeHttpRequests(auth -> {
                    // Permitir acceso a la raíz y a la página de login sin autenticación
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/login").anonymous();  // Permitir acceso a /login solo si no está autenticado
                    // Solo usuarios autenticados pueden acceder a las rutas de la API
                    auth.requestMatchers("/api/user/**").authenticated();
                    auth.requestMatchers("/api/user/home").authenticated();
                    auth.anyRequest().authenticated(); // Todos los demás endpoints requieren autenticación
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry())
                        .expiredUrl("/login?error=session_expired") // URL de redirección cuando la sesión ha expirado
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(successHandler)
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(withDefaults()) // Configuración predeterminada para el login de formulario
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
