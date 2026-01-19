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

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @PostMapping("/login")
    public ResponseEntity<UsuarioDto> login(@RequestBody LoginDto loginDto) {

        try {
            UsuarioDto usuarioDto = usuarioService.login(loginDto);
            return ResponseEntity.ok(usuarioDto);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }
    }
    @PostMapping("/registrar/repartidor")
    public ResponseEntity<UsuarioDto> registrarRepartidor(@RequestBody Usuario nuevoUsuario) {
        UsuarioDto usuarioDto = usuarioService.registrarUsuario(nuevoUsuario, "REPARTIDOR");
        return ResponseEntity.ok(usuarioDto);
    }
    @PostMapping("/registrar/gestor")
    public ResponseEntity<UsuarioDto> registrarGestor(@RequestBody Usuario nuevoUsuario) {
        UsuarioDto usuarioDto = usuarioService.registrarUsuario(nuevoUsuario, "GESTOR");
        return ResponseEntity.ok(usuarioDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> actualizarUsuario(
            @PathVariable Integer id,
            @RequestBody UsuarioDto usuarioDto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuarioDto));
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
        // Llamamos al servicio que ya programamos antes (el que guarda historial)
        usuarioService.actualizarUbicacion(idRepartidor, latitud, longitud);

        return ResponseEntity.ok("Ubicación recibida correctamente");
    }
    private UsuarioDto convertirAUsuarioDto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol().getRol());
        dto.setIdLicencia(usuario.getNegocio().getIdLicencia());
        if (usuario.getRol() != null) {
            dto.setRolId(usuario.getRol().getId());
            dto.setRol(usuario.getRol().getRol());
        }
        return dto;
    }
}