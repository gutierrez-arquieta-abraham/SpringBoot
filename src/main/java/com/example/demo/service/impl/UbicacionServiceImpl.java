package com.example.demo.service.impl;

import com.example.demo.dto.UbicacionDto;
import com.example.demo.model.Pedido;
import com.example.demo.model.UbicacionPedido;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.UbicacionRepository;
import com.example.demo.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private PedidoRepository pedidoRepository; // ¡Lo necesitamos para buscar el pedido!

    @Override
    public void guardarUbicacion(UbicacionDto ubicacionDto) {
        // 1. Buscar el pedido al que pertenece esta ubicación
        Pedido pedido = pedidoRepository.findById(ubicacionDto.getNumOrd())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // 2. Crear el nuevo objeto UbicacionPedido
        UbicacionPedido nuevaUbicacion = new UbicacionPedido();
        nuevaUbicacion.setPedido(pedido);
        nuevaUbicacion.setLatitud(ubicacionDto.getLatitud());
        nuevaUbicacion.setLongitud(ubicacionDto.getLongitud());

        // 3. Guardar (El timestamp se pone solo si usaste @CreationTimestamp)
        ubicacionRepository.save(nuevaUbicacion);
        // No devolvemos nada
    }

    @Override
    public List<UbicacionDto> getRutaPorPedido(Integer numOrd) {
        // Buscamos en la BD y convertimos la lista a una lista de DTOs
        return ubicacionRepository.findByPedido_NumOrdOrderByTimestampDesc(numOrd)
                .stream()
                .map(this::convertirADto) // Llama al convertidor
                .collect(Collectors.toList());
    }

    // --- Convertidor ---
    private UbicacionDto convertirADto(UbicacionPedido ubicacion) {
        UbicacionDto dto = new UbicacionDto();
        dto.setNumOrd(ubicacion.getPedido().getNumOrd());
        dto.setLatitud(ubicacion.getLatitud());
        dto.setLongitud(ubicacion.getLongitud());
        // (Podríamos agregar el timestamp si el Gestor lo necesita)
        return dto;
    }
    @Override
    public void eliminarUbicacion(Long idUbicacion) {
        // Necesita el repositorio de ubicación
        ubicacionRepository.deleteById(idUbicacion);
    }
    @Override
    public List<UbicacionDto> obtenerUbicacionesActivas() {
        return ubicacionRepository.findUltimasUbicaciones()
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }
}