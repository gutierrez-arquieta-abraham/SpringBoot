package com.example.demo.controler;

import com.example.demo.dto.UbicacionDto;
import com.example.demo.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ubicacion")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    @PostMapping
    public ResponseEntity<Void> guardarUbicacion(@RequestBody UbicacionDto ubicacionDto) {
        ubicacionService.guardarUbicacion(ubicacionDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pedido/{numOrd}")
    public ResponseEntity<List<UbicacionDto>> getRutaDelPedido(@PathVariable Integer numOrd) {
        return ResponseEntity.ok(ubicacionService.getRutaPorPedido(numOrd));
    }

    // --- NUEVO ENDPOINT PARA EL MAPA ---
    // GET /api/ubicacion/activos
    @GetMapping("/activos")
    public ResponseEntity<List<UbicacionDto>> getActivos() {
        return ResponseEntity.ok(ubicacionService.getRepartidoresActivos());
    }

    @DeleteMapping("/{idUbicacion}")
    public ResponseEntity<Void> eliminarUbicacion(@PathVariable Long idUbicacion) {
        ubicacionService.eliminarUbicacion(idUbicacion);
        return ResponseEntity.ok().build();
    }

}