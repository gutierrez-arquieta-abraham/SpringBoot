package com.example.demo.dto;

import lombok.Data;

@Data
public class NegocioDto {
    private Integer idLicencia;
    private String nomEmp;
    private String rfcEnc;
    private String direccion;
    private Integer zonaCobertura; // <-- Ahora es Integer
    private String codigoLicencia;
    private String codigoConexion;
    private Double latitud;
    private Double longitud;

}