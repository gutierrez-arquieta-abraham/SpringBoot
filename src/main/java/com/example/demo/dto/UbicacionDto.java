package com.example.demo.dto;

import lombok.Data;

@Data
public class UbicacionDto {
    private Integer numOrd; // El ID del pedido que se está rastreando
    private Double latitud;
    private Double longitud;
    private String estatus;
}