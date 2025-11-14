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

    // --- ENDPOINT DE ESCRITURA ---
    // (POST /api/ubicacion)
    // La app del Repartidor llamará a esto cada X segundos
    @PostMapping
    public ResponseEntity<Void> guardarUbicacion(@RequestBody UbicacionDto ubicacionDto) {
        ubicacionService.guardarUbicacion(ubicacionDto);
        // Devolvemos un 200 OK vacío.
        return ResponseEntity.ok().build();
    }

    // --- ENDPOINT DE LECTURA ---
    // (GET /api/ubicacion/pedido/10)
    // La app del Gestor llama a esto para dibujar la ruta del pedido 10
    @GetMapping("/pedido/{numOrd}")
    public ResponseEntity<List<UbicacionDto>> getRutaDelPedido(@PathVariable Integer numOrd) {
        List<UbicacionDto> ruta = ubicacionService.getRutaPorPedido(numOrd);
        return ResponseEntity.ok(ruta);
    }
}