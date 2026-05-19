package com.example.demo.controler;

import com.example.demo.dto.PedidoDto;
import com.example.demo.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // --- ARREGLO 1: Ahora recibe estrictamente PedidoDto ---
    @PostMapping
    public ResponseEntity<PedidoDto> crearPedido(@RequestBody PedidoDto nuevoPedidoDto) {
        return ResponseEntity.ok(pedidoService.crearPedido(nuevoPedidoDto));
    }

    @PutMapping("/{numOrd}/asignar/{idRepartidor}")
    public ResponseEntity<PedidoDto> asignarPedido(
            @PathVariable Integer numOrd,
            @PathVariable Integer idRepartidor) {
        return ResponseEntity.ok(pedidoService.asignarRepartidor(numOrd, idRepartidor));
    }

    @GetMapping("/repartidor/{idRepartidor}")
    public ResponseEntity<List<PedidoDto>> getPedidosPorRepartidor(@PathVariable Integer idRepartidor) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorRepartidor(idRepartidor));
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoDto>> obtenerMisPedidos(@RequestParam Integer idRepartidor) {
        List<PedidoDto> pedidos = pedidoService.obtenerPedidosPorRepartidor(idRepartidor);
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/negocio/{idLicencia}")
    public ResponseEntity<List<PedidoDto>> getPedidosPorNegocio(@PathVariable Integer idLicencia) {
        return ResponseEntity.ok(pedidoService.getPedidosPorNegocio(idLicencia));
    }

    @PutMapping("/{numOrd}")
    public ResponseEntity<PedidoDto> actualizarPedido(
            @PathVariable Integer numOrd,
            @RequestBody PedidoDto pedidoDto) {
        return ResponseEntity.ok(pedidoService.actualizarPedido(numOrd, pedidoDto));
    }

    @DeleteMapping("/{numOrd}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Integer numOrd) {
        pedidoService.eliminarPedido(numOrd);
        return ResponseEntity.ok().build();
    }

    // --- ARREGLO 2: Redirigido al método oficial 'actualizarEstatus' ---
    @PostMapping("/actualizar-estado")
    public ResponseEntity<PedidoDto> actualizarEstado(
            @RequestParam Integer numOrd,
            @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(pedidoService.actualizarEstatus(numOrd, nuevoEstado));
    }

    @GetMapping("/negocio/{idLicencia}/historial")
    public ResponseEntity<List<PedidoDto>> getHistorialNegocio(@PathVariable Integer idLicencia) {
        return ResponseEntity.ok(pedidoService.obtenerHistorialNegocio(idLicencia));
    }

    @GetMapping("/repartidor/{id}/historial")
    public ResponseEntity<List<PedidoDto>> getHistorialRepartidor(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.obtenerHistorialRepartidor(id));
    }

    // ARREGLO 3: Se eliminó el método "obtenerEstadisticas" por ID de pedido,
    // así como los Repositorios inyectados incorrectamente.
}