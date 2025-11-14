package com.example.demo.dto;

import lombok.Data;
// (Agrega otras anotaciones de lombok si las usas)

@Data
public class UsuarioDto {

    private Integer id;
    private String nombre;
    private String email;
    private String rfc;
    private String rol; // Solo el nombre del rol, no el objeto entero
    private Integer idLicencia; // El ID del negocio al que pertenece

    // ¡NUNCA INCLUIMOS LA CONTRASEÑA AQUÍ!
}