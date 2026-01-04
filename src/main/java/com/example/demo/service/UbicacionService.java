package com.example.demo.service;

import com.example.demo.dto.UbicacionDto;
import java.util.List;

public interface UbicacionService {
    void guardarUbicacion(UbicacionDto ubicacionDto);
    List<UbicacionDto> getRutaPorPedido(Integer numOrd);
    void eliminarUbicacion(Long idUbicacion);

    // --- NUEVO ---
    List<UbicacionDto> getRepartidoresActivos();
}