package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PedidoDto {
    // Info del Pedido
    private Integer numOrd;
    private String descripcion;
    private String destino;

    // EL ÚNICO ESTADO QUE SOBREVIVE (Debe coincidir con la entidad)
    private String estadoReal;

    // LA NUEVA MÉTRICA DE TIEMPOS ESTRICTA
    private LocalDateTime fechaHoraCreacion;
    private LocalDateTime fechaHoraRecogida;
    private LocalDateTime fechaHoraEntrega;

    // Coordenadas para el mapa
    private Double latitud;
    private Double longitud;

    // Info del Negocio
    private Integer idLicencia;
    private String nombreNegocio;

    // Info del Repartidor
    private Integer idRepartidor;
    private String nombreRepartidor;
}