package com.example.demo.controler;

import com.example.demo.dto.PedidoDto;
import com.example.demo.model.Pedido;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.demo.model.Usuario;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // (POST /api/pedidos) - El Gestor crea un pedido
    @PostMapping
    public ResponseEntity<PedidoDto> crearPedido(@RequestBody Pedido nuevoPedido) {
        // El JSON debe traer: "descripcion", "destino" y "negocio": { "idLicencia": 1 }
        return ResponseEntity.ok(pedidoService.crearPedido(nuevoPedido));
    }

    // (PUT /api/pedidos/10/asignar/5) - Asigna pedido 10 a repartidor 5
    @PutMapping("/{numOrd}/asignar/{idRepartidor}")
    public ResponseEntity<PedidoDto> asignarPedido(
            @PathVariable Integer numOrd,
            @PathVariable Integer idRepartidor) {
        // Delegamos TODA la lógica al servicio (validaciones, cambios de estado, etc.)
        return ResponseEntity.ok(pedidoService.asignarRepartidor(numOrd, idRepartidor));
    }

    // (PUT /api/pedidos/10/estado/Entregado) - Actualiza estado de pedido 10
    @PutMapping("/{numOrd}/estado/{nuevoEstado}")
    public ResponseEntity<PedidoDto> actualizarEstado(
            @PathVariable Integer numOrd,
            @PathVariable String nuevoEstado) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(numOrd, nuevoEstado));
    }

    // (GET /api/pedidos/repartidor/5) - El Repartidor 5 ve sus pedidos
    @GetMapping("/repartidor/{idRepartidor}")
    public ResponseEntity<List<PedidoDto>> getPedidosPorRepartidor(@PathVariable Integer idRepartidor) {
        return ResponseEntity.ok(pedidoService.getPedidosPorRepartidor(idRepartidor));
    }

    // (GET /api/pedidos/negocio/1) - El Gestor del Negocio 1 ve sus pedidos
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

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
}