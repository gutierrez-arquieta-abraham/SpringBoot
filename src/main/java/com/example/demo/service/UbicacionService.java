package com.example.demo.service;

import com.example.demo.dto.UbicacionDto;
import java.util.List;

public interface UbicacionService {
    void guardarUbicacion(UbicacionDto ubicacionDto);
    List<UbicacionDto> getRutaPorPedido(Integer numOrd);
    List<UbicacionDto> getRepartidoresActivos(); // Radar
    void eliminarUbicacion(Long idUbicacion);
}