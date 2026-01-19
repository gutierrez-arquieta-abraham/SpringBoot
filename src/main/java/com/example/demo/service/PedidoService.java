package com.example.demo.service;

import com.example.demo.dto.PedidoDto;
import com.example.demo.model.Pedido;
import java.util.List;

public interface PedidoService {

    PedidoDto crearPedido(Pedido pedido);

    PedidoDto asignarRepartidor(Integer numOrd, Integer idRepartidor);

    // --- 👇 ESTA ES LA LÍNEA QUE TE FALTABA Y CAUSA EL ERROR 👇 ---
    PedidoDto actualizarEstatus(Integer idPedido, String nuevoEstatus);
    // --------------------------------------------------------------
    List<PedidoDto> obtenerHistorialRepartidor(Integer idRepartidor);
    List<PedidoDto> obtenerHistorialNegocio(Integer idLicencia);

    // Este lo dejamos por si alguna parte vieja de tu código lo llama
    PedidoDto actualizarEstado(Integer numOrd, String nuevoEstado);

    List<PedidoDto> obtenerPedidosPorRepartidor(Integer idRepartidor);

    List<PedidoDto> getPedidosPorNegocio(Integer idLicencia);

    void eliminarPedido(Integer numOrd);

    PedidoDto actualizarPedido(Integer numOrd, PedidoDto dto);
}