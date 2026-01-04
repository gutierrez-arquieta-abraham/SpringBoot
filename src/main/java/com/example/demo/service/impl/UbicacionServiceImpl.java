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
    private PedidoRepository pedidoRepository;

    @Override
    public void guardarUbicacion(UbicacionDto ubicacionDto) {
        Pedido pedido = pedidoRepository.findById(ubicacionDto.getNumOrd())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        UbicacionPedido nueva = new UbicacionPedido();
        nueva.setPedido(pedido);
        nueva.setLatitud(ubicacionDto.getLatitud());
        nueva.setLongitud(ubicacionDto.getLongitud());
        ubicacionRepository.save(nueva);
    }

    @Override
    public List<UbicacionDto> getRutaPorPedido(Integer numOrd) {
        return ubicacionRepository.findByPedido_NumOrdOrderByTimestampDesc(numOrd)
                .stream().map(this::convertirADto).collect(Collectors.toList());
    }

    @Override
    public void eliminarUbicacion(Long idUbicacion) {
        ubicacionRepository.deleteById(idUbicacion);
    }

    // --- IMPLEMENTACIÓN DEL RADAR ---
    @Override
    public List<UbicacionDto> getRepartidoresActivos() {
        return ubicacionRepository.findUltimasUbicaciones()
                .stream().map(this::convertirADto).collect(Collectors.toList());
    }

    private UbicacionDto convertirADto(UbicacionPedido u) {
        UbicacionDto dto = new UbicacionDto();
        dto.setNumOrd(u.getPedido().getNumOrd());
        dto.setLatitud(u.getLatitud());
        dto.setLongitud(u.getLongitud());
        // Aquí podrías agregar el estado si lo tuvieras en el modelo
        dto.setEstatus("EN_CAMINO");
        return dto;
    }
}