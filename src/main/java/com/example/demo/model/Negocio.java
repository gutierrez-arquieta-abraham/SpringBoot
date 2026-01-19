package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "negocio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Negocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_licencia")
    private Integer idLicencia;

    @Column(name = "nom_emp")
    private String nomEmp;

    @Column(name = "RFC_enc")
    private String rfcEnc;

    @Column(name = "direccion")
    private String direccion;

    // --- AQUÍ ESTÁ EL CAMBIO A ENTERO ---
    @Column(name = "zona_cobertura")
    private Integer zonaCobertura;

    @Column(name = "codigo_licencia", unique = true)
    private String codigoLicencia;

    @Column(name = "codigo_conexion", unique = true)
    private String codigoConexion;
}