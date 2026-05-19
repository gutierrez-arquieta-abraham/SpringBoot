package com.example.demo.controler;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<UsuarioDto> login(@RequestBody LoginDto loginDto) {
        try {
            return ResponseEntity.ok(usuarioService.login(loginDto));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }
    }

    // --- CORRECCIÓN 1: Ahora recibe UsuarioDto estricto ---
    @PostMapping("/registrar/repartidor")
    public ResponseEntity<UsuarioDto> registrarRepartidor(@RequestBody UsuarioDto nuevoUsuarioDto) {
        return ResponseEntity.ok(usuarioService.registrarUsuario(nuevoUsuarioDto, "REPARTIDOR"));
    }

    @PostMapping("/registrar/gestor")
    public ResponseEntity<UsuarioDto> registrarGestor(@RequestBody UsuarioDto nuevoUsuarioDto) {
        return ResponseEntity.ok(usuarioService.registrarUsuario(nuevoUsuarioDto, "GESTOR"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> actualizarUsuario(
            @PathVariable Integer id,
            @RequestBody UsuarioDto usuarioDto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuarioDto));
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<Void> recuperarPassword(@RequestBody Map<String, String> datos) {
        String email = datos.get("email");
        String nuevaContrasena = datos.get("nuevaContrasena");

        if (email == null || nuevaContrasena == null) {
            return ResponseEntity.badRequest().build();
        }

        usuarioService.recuperarContrasenaPorEmail(email, nuevaContrasena);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/cambiarContrasena/{id}")
    public ResponseEntity<Void> cambiarContrasena(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {

        String nuevaContrasena = body.get("contrasena");
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

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> getUsuarioById(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    // CORRECCIÓN 2: Se eliminó el método redundante PUT cambiarEstatus.
    // Tienes POST /actualizar-estatus haciendo esto mismo de forma oficial abajo.

    @GetMapping("/negocio/{idLicencia}/repartidores")
    public ResponseEntity<List<UsuarioDto>> getRepartidoresPorNegocio(@PathVariable Integer idLicencia) {
        return ResponseEntity.ok(usuarioService.obtenerRepartidoresPorNegocio(idLicencia));
    }

    @PostMapping("/vincular-equipo")
    public ResponseEntity<UsuarioDto> unirseAEquipo(@RequestParam Integer idUsuario, @RequestParam String codigo) {
        return ResponseEntity.ok(usuarioService.vincularRepartidorPorCodigo(idUsuario, codigo));
    }

    @PostMapping("/actualizar-estatus")
    public ResponseEntity<UsuarioDto> actualizarEstatus(
            @RequestParam Integer idUsuario,
            @RequestParam String nuevoEstatus) {
        return ResponseEntity.ok(usuarioService.actualizarEstatus(idUsuario, nuevoEstatus));
    }

    @PostMapping("/actualizar-ubicacion")
    public ResponseEntity<String> actualizarUbicacion(
            @RequestParam("idRepartidor") Integer idRepartidor,
            @RequestParam("latitud") Double latitud,
            @RequestParam("longitud") Double longitud
    ) {
        usuarioService.actualizarUbicacion(idRepartidor, latitud, longitud);
        return ResponseEntity.ok("Ubicación recibida correctamente");
    }

    // CORRECCIÓN 3: Se purgó el método privado convertirAUsuarioDto()
}