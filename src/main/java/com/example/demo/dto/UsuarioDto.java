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
    private String contrasenaTransitoria;
    private String rol;
    private Integer rolId;
    private Integer idLicencia;
    private Double latitudActual;
    private Double longitudActual;

    // --- EL NUEVO CAMPO PARA EL TOKEN ---
    private String token;
}