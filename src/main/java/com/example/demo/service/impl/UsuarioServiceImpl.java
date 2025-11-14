package com.example.demo.service.impl;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // ¡Importante!
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PasswordEncoder passwordEncoder; // ¡Arreglado!

    @Override
    public UsuarioDto registrarUsuario(Usuario nuevoUsuario, String nombreRol) {
        // 1. Buscar el Rol (¡asegúrate de haberlo creado con Postman!)
        Rol rol = rolRepository.findByRol(nombreRol)
                .orElseThrow(() -> new RuntimeException("Error: Rol '" + nombreRol + "' no encontrado."));

        // 2. Asignar el rol y encriptar contraseña
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setContrasena(passwordEncoder.encode(nuevoUsuario.getContrasena()));

        // 3. Guardar
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        return convertirAUsuarioDto(usuarioGuardado);
    }

    @Override
    public UsuarioDto login(LoginDto loginDto) {
        // 1. Buscar al usuario por email
        Usuario usuario = usuarioRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));

        // 2. Comprobar contraseña
        if (passwordEncoder.matches(loginDto.getContrasena(), usuario.getContrasena())) {
            // ¡Login exitoso! Devolvemos el DTO
            return convertirAUsuarioDto(usuario);
        } else {
            // Contraseña incorrecta
            throw new RuntimeException("Email o contraseña incorrectos");
        }
    }

    // --- MÉTODO PRIVADO PARA CONVERTIR A DTO ---
    private UsuarioDto convertirAUsuarioDto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRfc(usuario.getRfc());
        dto.setRol(usuario.getRol().getRol()); // Obtenemos "GESTOR" o "REPARTIDOR"
        dto.setIdLicencia(usuario.getNegocio().getIdLicencia());
        return dto;
    }
}