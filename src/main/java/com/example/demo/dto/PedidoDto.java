package com.example.demo.dto;

import lombok.Data;

@Data
public class PedidoDto {
    // Info del Pedido
    private Integer numOrd;
    private String descripcion;

    // --- AGREGAR ESTA VARIABLE QUE FALTABA ---
    private String estatus; // Para compatibilidad con el código viejo
    // -----------------------------------------

    private String estado;     // Para mostrar en pantalla
    private String estadoReal; // Para lógica interna ("EN_CURSO", "ENTREGADO")

    private String destino;
    private String fechaEntrega; // String para evitar problemas de formato
    private String horaEntrega;

    // Coordenadas para el mapa
    private Double latitud;
    private Double longitud;

    // Info del Negocio (Solo ID y Nombre, no el objeto entero)
    private Integer idLicencia;
    private String nombreNegocio;

    // Info del Repartidor
    private Integer idRepartidor;
    private String nombreRepartidor;
}