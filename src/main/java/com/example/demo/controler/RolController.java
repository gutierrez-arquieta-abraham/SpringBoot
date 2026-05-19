package com.example.demo.controler;

import com.example.demo.dto.RolDto;
// Importante: Ya no importamos com.example.demo.model.Rol;
import com.example.demo.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    // --- ARREGLO: Recibe y devuelve RolDto ---
    @PostMapping
    public ResponseEntity<RolDto> crearRol(@RequestBody RolDto rolDto) {
        RolDto rolGuardado = rolService.crearRol(rolDto);
        return ResponseEntity.ok(rolGuardado);
    }

    // --- ARREGLO: Recibe y devuelve RolDto ---
    @PutMapping("/{id}")
    public ResponseEntity<RolDto> actualizarRol(@PathVariable Integer id, @RequestBody RolDto rolDto) {
        RolDto rolActualizado = rolService.actualizarRol(id, rolDto);
        return ResponseEntity.ok(rolActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRol(@PathVariable Integer id) {
        rolService.eliminarRol(id);
        return ResponseEntity.ok().build();
    }
}