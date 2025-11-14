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
@Table(name = "Negocio")
public class Negocio {

    @Id
    @Column(name = "ID_licencia")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLicencia;

    @Column(name = "nom_emp")
    private String nomEmp;

    @Column(name = "RFC_enc", unique = true)
    private String rfcEnc;
}