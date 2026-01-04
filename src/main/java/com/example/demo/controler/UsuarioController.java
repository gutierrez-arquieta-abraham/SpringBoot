package com.example.demo.controler;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.Usuario; // O un "RegistroDto" si lo prefieres
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // --- ENDPOINT DE LOGIN ---
    // POST /api/usuarios/login
    @PostMapping("/login")
    public ResponseEntity<UsuarioDto> login(@RequestBody LoginDto loginDto) {
        // --- 🕵️‍♂️ ZONA DE ESPIONAJE (BORRAR DESPUÉS) ---
        System.out.println("------------------------------------------------");
        System.out.println("🔍 INTENTO DE LOGIN RECIBIDO");
        System.out.println("📧 Email recibido: '" + loginDto.getEmail() + "'");
        System.out.println("🔑 Password recibido: '" + loginDto.getContrasena() + "'");
        // ------------------------------------------------

        try {
            UsuarioDto usuarioDto = usuarioService.login(loginDto);
            System.out.println("✅ ¡Login Exitoso!");
            return ResponseEntity.ok(usuarioDto);
        } catch (RuntimeException e) {
            // Imprimimos el error real en la consola para verlo nosotros
            System.out.println("❌ ERROR LOGIN: " + e.getMessage());
            e.printStackTrace(); // <-- ESTO NOS DIRÁ SI ES "NO ENCONTRADO" O "PASSWORD MAL"
            return ResponseEntity.status(401).build();
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
    @PutMapping("/{id}/estatus/{nuevoEstatus}")
    public ResponseEntity<Void> cambiarEstatus(
            @PathVariable Integer id,
            @PathVariable String nuevoEstatus) {

        usuarioService.cambiarEstatus(id, nuevoEstatus);
        return ResponseEntity.ok().build();
    }
    // PATCH /api/usuarios/{id}/ubicacion?lat=19.4326&lon=-99.1332
    @PatchMapping("/{id}/ubicacion")
    public ResponseEntity<Void> actualizarUbicacion(
            @PathVariable Integer id,
            @RequestParam Double lat,
            @RequestParam Double lon) {

        usuarioService.actualizarUbicacionRepartidor(id, lat, lon);
        return ResponseEntity.ok().build();
    }
    // GET /api/usuarios/negocio/1/repartidores
    @GetMapping("/negocio/{idLicencia}/repartidores")
    public ResponseEntity<List<UsuarioDto>> getRepartidoresPorNegocio(@PathVariable Integer idLicencia) {
        // ¡Ahora llamamos al SERVICIO, no al repositorio!
        return ResponseEntity.ok(usuarioService.obtenerRepartidoresPorNegocio(idLicencia));
    }

    // Método auxiliar si no lo tienes público en el servicio
    private UsuarioDto convertirAUsuarioDto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol().getRol());
        dto.setIdLicencia(usuario.getNegocio().getIdLicencia());
        return dto;
    }
}