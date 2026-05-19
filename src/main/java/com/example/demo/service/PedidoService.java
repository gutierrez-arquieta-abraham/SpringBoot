package com.example.demo.service;

import com.example.demo.dto.DashboardNegocioDto;
import com.example.demo.dto.PedidoDto;
import java.util.List;

public interface PedidoService {
    // Regla de oro: Recibir DTO
    PedidoDto crearPedido(PedidoDto dto);

    // Implementación real, nada de arrojar excepciones
    PedidoDto actualizarPedido(Integer numOrd, PedidoDto dto);

    PedidoDto asignarRepartidor(Integer numOrd, Integer idRepartidor);
    PedidoDto actualizarEstatus(Integer idPedido, String nuevoEstatus);
    void eliminarPedido(Integer numOrd);

    List<PedidoDto> obtenerPedidosPorRepartidor(Integer idRepartidor);
    List<PedidoDto> getPedidosPorNegocio(Integer idLicencia);
    List<PedidoDto> obtenerHistorialRepartidor(Integer idRepartidor);
    List<PedidoDto> obtenerHistorialNegocio(Integer idLicencia);

    DashboardNegocioDto generarDashboardAnalitico(Integer idLicencia);
    DashboardNegocioDto generarDashboardAnaliticoRepartidor(Integer idRepartidor);
}