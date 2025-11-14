package com.example.demo.controler;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.Usuario; // O un "RegistroDto" si lo prefieres
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // --- ENDPOINT DE LOGIN ---
    // POST /api/usuarios/login
    @PostMapping("/login")
    public ResponseEntity<UsuarioDto> login(@RequestBody LoginDto loginDto) {
        try {
            UsuarioDto usuarioDto = usuarioService.login(loginDto);
            return ResponseEntity.ok(usuarioDto);
        } catch (RuntimeException e) {
            // Login fallido (email o pass incorrectos)
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }
    }

    // --- ENDPOINT DE REGISTRO (REPARTIDOR) ---
    // POST /api/usuarios/registrar/repartidor
    @PostMapping("/registrar/repartidor")
    public ResponseEntity<UsuarioDto> registrarRepartidor(@RequestBody Usuario nuevoUsuario) {
        // (El JSON debe incluir el "negocio": { "idLicencia": 1 } )
        UsuarioDto usuarioDto = usuarioService.registrarUsuario(nuevoUsuario, "REPARTIDOR");
        return ResponseEntity.ok(usuarioDto);
    }

    // --- ENDPOINT DE REGISTRO (GESTOR) ---
    // POST /api/usuarios/registrar/gestor
    @PostMapping("/registrar/gestor")
    public ResponseEntity<UsuarioDto> registrarGestor(@RequestBody Usuario nuevoUsuario) {
        // (El JSON debe incluir el "negocio": { "idLicencia": 1 } )
        UsuarioDto usuarioDto = usuarioService.registrarUsuario(nuevoUsuario, "GESTOR");
        return ResponseEntity.ok(usuarioDto);
    }
}