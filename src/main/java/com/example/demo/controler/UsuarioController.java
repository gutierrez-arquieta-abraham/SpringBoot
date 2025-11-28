package com.example.demo.controler;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.Usuario; // O un "RegistroDto" si lo prefieres
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    // PUT: Actualiza datos generales (nombre, email, rfc)
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> actualizarUsuario(
            @PathVariable Integer id,
            @RequestBody UsuarioDto usuarioDto) {

        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuarioDto));
    }

    // PATCH: Solo actualiza la contraseña (recibe un mapa simple con "contrasena")
    @PatchMapping("/cambiarContrasena/{id}")
    public ResponseEntity<Void> cambiarContrasena(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {

        String nuevaContrasena = body.get("contrasena");
        // Simple validación de campo
        if (nuevaContrasena == null || nuevaContrasena.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        usuarioService.actualizarContrasena(id, nuevaContrasena);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok().build();
    }
    // GET /api/usuarios/5
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> getUsuarioById(@PathVariable Integer id) {
        // Asumiendo que agregamos el método getUsuarioById al servicio
        UsuarioDto usuarioDto = usuarioService.getUsuarioById(id);
        return ResponseEntity.ok(usuarioDto);
    }
}