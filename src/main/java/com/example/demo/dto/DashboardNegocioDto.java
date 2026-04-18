package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class DashboardNegocioDto {
    // KPIs Generales
    private Double promedioTiempoEntrega; // Promedio en minutos
    private Double totalKilometrosRecorridos; // Suma total de distancia
    private Integer totalPedidosEntregados; // Cantidad de éxitos

    // Lista para las gráficas en Android (ej. últimos 7 días)
    private List<PedidoDto> historialReciente;
}