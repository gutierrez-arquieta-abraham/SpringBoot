package com.example.demo.service;

import com.example.demo.dto.UbicacionDto;
import java.util.List;

public interface UbicacionService {

    // El Repartidor (la app) guarda su ubicación
    void guardarUbicacion(UbicacionDto ubicacionDto);

    // El Gestor (la app) lee la ruta de un pedido
    List<UbicacionDto> getRutaPorPedido(Integer numOrd);
    void eliminarUbicacion(Long idUbicacion);
    List<UbicacionDto> obtenerUbicacionesActivas();
}