package com.example.demo.service;

import com.example.demo.dto.PedidoDto;
import com.example.demo.model.Pedido;
import java.util.List;

public interface PedidoService {

    // El Gestor crea un pedido (recibe el modelo, devuelve DTO)
    PedidoDto crearPedido(Pedido nuevoPedido);

    // El Gestor asigna un pedido a un repartidor
    PedidoDto asignarRepartidor(Integer numOrd, Integer idRepartidor);

    // El Repartidor actualiza el estado (ej: "Entregado")
    PedidoDto actualizarEstado(Integer numOrd, String nuevoEstado);

    // El Repartidor ve sus pedidos
    List<PedidoDto> getPedidosPorRepartidor(Integer idRepartidor);

    // El Gestor ve todos los pedidos de su negocio
    List<PedidoDto> getPedidosPorNegocio(Integer idLicencia);
}