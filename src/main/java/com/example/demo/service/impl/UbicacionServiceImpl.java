package com.example.demo.service.impl;

import com.example.demo.dto.UbicacionDto;
import com.example.demo.model.Pedido;
import com.example.demo.model.UbicacionPedido;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.UbicacionRepository;
import com.example.demo.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UbicacionServiceImpl implements UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    public void guardarUbicacion(UbicacionDto ubicacionDto) {
        Pedido pedido = pedidoRepository.findById(ubicacionDto.getNumOrd())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado para registrar ubicación"));

        UbicacionPedido nueva = new UbicacionPedido();
        nueva.setPedido(pedido);
        nueva.setLatitud(ubicacionDto.getLatitud());
        nueva.setLongitud(ubicacionDto.getLongitud());

        ubicacionRepository.save(nueva);
    }

    @Override
    @Transactional(readOnly = true) // Optimización: Le decimos a Spring que esto es solo lectura
    public List<UbicacionDto> getRutaPorPedido(Integer numOrd) {
        return ubicacionRepository.findByPedido_NumOrdOrderByTimestampDesc(numOrd)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UbicacionDto> getRepartidoresActivos() {
        return ubicacionRepository.findUltimasUbicaciones()
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarUbicacion(Long idUbicacion) {
        if (!ubicacionRepository.existsById(idUbicacion)) {
            throw new RuntimeException("No se puede eliminar: Ubicación no encontrada");
        }
        ubicacionRepository.deleteById(idUbicacion);
    }

    // --- CONVERSOR PRIVADO ---
    private UbicacionDto convertirADto(UbicacionPedido u) {
        UbicacionDto dto = new UbicacionDto();
        dto.setNumOrd(u.getPedido().getNumOrd());
        dto.setLatitud(u.getLatitud());
        dto.setLongitud(u.getLongitud());
        dto.setEstatus(u.getPedido().getEstadoReal()); // Extraemos el estatus real del pedido
        return dto;
    }
}