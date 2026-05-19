package com.example.demo.security;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // El token estándar viaja como: "Bearer eyJhbGciOiJIUzI1NiIsIn..."
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.extractEmail(jwt);
            } catch (Exception e) {
                logger.error("Error al parsear o validar el token JWT enviado");
            }
        }

        // Si encontramos el email y no hay una autenticación previa en el hilo de la ejecución
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndActivoTrue(email);

            if (usuarioOpt.isPresent() && jwtUtil.validateToken(jwt, email)) {
                Usuario usuario = usuarioOpt.get();

                // Mapeamos el rol con el prefijo estándar ROLE_ (ej: ROLE_REPARTIDOR)
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getRol().toUpperCase())
                );

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        email, null, authorities
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Inyectamos al usuario autenticado en el contexto de Spring
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}