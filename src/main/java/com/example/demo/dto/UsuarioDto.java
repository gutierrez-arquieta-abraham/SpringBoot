package com.example.demo.dto;

import lombok.Data;

@Data
public class UsuarioDto {
    private Integer id;
    private String nombre;
    private String email;
    private String rfc;
    private String estatus;
    private String telefono;

    // --- AGREGAMOS ESTOS DOS PARA QUE NO FALLE ---
    private String rol;    // Para el texto "GESTOR" o "REPARTIDOR" (Corrige el error del Controller)
    private Integer rolId; // Para el ID numérico (1 o 2)

    private Integer idLicencia;

    private Double latitudActual;
    private Double longitudActual;
}