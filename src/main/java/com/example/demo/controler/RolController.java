package com.example.demo.controler;

import com.example.demo.model.Rol;
import com.example.demo.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    // --- ¡AQUÍ! ---
    @Autowired // <-- ¿Está esto aquí?
    private RolService rolService;

    @PostMapping
    public ResponseEntity<Rol> crearRol(@RequestBody Rol rol) {
        // Si falta el @Autowired, rolService es NULL y esta línea truena
        Rol rolGuardado = rolService.crearRol(rol);
        return ResponseEntity.ok(rolGuardado);
    }
}