package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "negocio")
public class Negocio {

    @Id
    @Column(name = "id_licencia")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLicencia;

    @Column(name = "nom_emp")
    private String nomEmp;

    @Column(name = "rfc_enc", unique = true)
    private String rfcEnc;

    // --- NUEVO CAMPO ---
    @Column(name = "codigo_licencia", unique = true)
    private String codigoLicencia;
}