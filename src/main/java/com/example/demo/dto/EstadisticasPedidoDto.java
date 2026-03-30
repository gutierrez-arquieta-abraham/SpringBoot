package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstadisticasPedidoDto {
    private long minutosTranscurridos;
    private double kilometrosRecorridos;
}