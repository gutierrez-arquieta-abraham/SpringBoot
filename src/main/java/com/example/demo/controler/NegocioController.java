package com.example.demo.controler;

import com.example.demo.dto.NegocioDto;
import com.example.demo.model.Negocio;
import com.example.demo.service.NegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/negocios")
public class NegocioController {

    @Autowired
    private NegocioService negocioService;

    @PostMapping
    public ResponseEntity<NegocioDto> crearNegocio(@RequestBody Negocio negocio) {
        // (Sería mejor recibir un DTO, pero esto es más simple)
        return ResponseEntity.ok(negocioService.crearNegocio(negocio));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NegocioDto> getNegocioById(@PathVariable Integer id) {
        return ResponseEntity.ok(negocioService.getNegocioById(id));
    }

    @GetMapping
    public ResponseEntity<List<NegocioDto>> getAllNegocios() {
        return ResponseEntity.ok(negocioService.getAllNegocios());
    }
    // --- ENDPOINT DE ACTUALIZACIÓN ---
    @PutMapping("/{id}")
    public ResponseEntity<NegocioDto> actualizarNegocio(
            @PathVariable Integer id,
            @RequestBody NegocioDto negocioDto) {

        return ResponseEntity.ok(negocioService.actualizarNegocio(id, negocioDto));
    }

    // --- ENDPOINT DE ELIMINACIÓN ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNegocio(@PathVariable Integer id) {
        negocioService.eliminarNegocio(id);
        // Regresamos 200 OK sin contenido
        return ResponseEntity.ok().build();
    }
}