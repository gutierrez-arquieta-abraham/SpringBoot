package com.example.demo.service.impl;

import com.example.demo.dto.PedidoDto;
import com.example.demo.model.Negocio;
import com.example.demo.model.Pedido;
import com.example.demo.model.Usuario;
import com.example.demo.repository.NegocioRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository; // Necesario para asignar
    @Autowired
    private NegocioRepository negocioRepository; // Necesario para crear

    @Override
    public PedidoDto crearPedido(Pedido nuevoPedido) {
        // 1. Buscar el negocio (asumimos que el JSON trae el negocio.idLicencia)
        Negocio negocio = negocioRepository.findById(nuevoPedido.getNegocio().getIdLicencia())
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));

        // 2. Settear los valores iniciales
        nuevoPedido.setNegocio(negocio);
        nuevoPedido.setEstado("Pendiente"); // Estado inicial
        // El repartidor (repartidorAsignado) es null al crear

        Pedido guardado = pedidoRepository.save(nuevoPedido);
        return convertirADto(guardado);
    }

    @Override
    public PedidoDto asignarRepartidor(Integer numOrd, Integer idRepartidor) {
        // 1. Buscar el pedido
        Pedido pedido = pedidoRepository.findById(numOrd)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        // 2. Buscar al repartidor
        Usuario repartidor = usuarioRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        // (Validación extra: checar que el rol del usuario sea "REPARTIDOR")
        if (!repartidor.getRol().getRol().equals("REPARTIDOR")) {
            throw new RuntimeException("El usuario no es un repartidor");
        }

        // 3. Asignar y actualizar estado
        pedido.setRepartidorAsignado(repartidor);
        pedido.setEstado("En camino");

        Pedido guardado = pedidoRepository.save(pedido);
        return convertirADto(guardado);
    }

    @Override
    public PedidoDto actualizarEstado(Integer numOrd, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(numOrd)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado); // Ej: "Entregado"
        // (Si es "Entregado", podrías poner la fecha y hora de entrega)

        Pedido guardado = pedidoRepository.save(pedido);
        return convertirADto(guardado);
    }

    @Override
    public List<PedidoDto> getPedidosPorRepartidor(Integer idRepartidor) {
        return pedidoRepository.findByRepartidorAsignado_Id(idRepartidor)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoDto> getPedidosPorNegocio(Integer idLicencia) {
        return pedidoRepository.findByNegocio_IdLicencia(idLicencia)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    // --- El Convertidor más complejo ---
    private PedidoDto convertirADto(Pedido pedido) {
        PedidoDto dto = new PedidoDto();
        dto.setNumOrd(pedido.getNumOrd());
        dto.setDescripcion(pedido.getDescripcion());
        dto.setEstado(pedido.getEstado());
        dto.setDestino(pedido.getDestino());
        dto.setFechaDeEntrega(pedido.getFechaDeEntrega());

        // Info del Negocio
        dto.setIdLicencia(pedido.getNegocio().getIdLicencia());
        dto.setNombreNegocio(pedido.getNegocio().getNomEmp());

        // Info del Repartidor (¡puede ser null!)
        if (pedido.getRepartidorAsignado() != null) {
            dto.setIdRepartidor(pedido.getRepartidorAsignado().getId());
            dto.setNombreRepartidor(pedido.getRepartidorAsignado().getNombre());
        }

        return dto;
    }
}