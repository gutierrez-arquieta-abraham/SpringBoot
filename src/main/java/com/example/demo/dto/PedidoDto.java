package com.example.demo.dto;

import lombok.Data;
import java.util.Date;

@Data
public class PedidoDto {
    // Info del Pedido
    private Integer numOrd;
    private String descripcion;
    private String estado;
    private String destino;
    private Date fechaDeEntrega;

    // Info del Negocio (de la relación)
    private Integer idLicencia;
    private String nombreNegocio;

    // Info del Repartidor (de la relación)
    private Integer idRepartidor;
    private String nombreRepartidor;
}