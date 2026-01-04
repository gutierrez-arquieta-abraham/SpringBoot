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
@Table(name = "Usuario") // Esto está perfecto
public class Usuario // Renombramos la clase
{
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NOMBRE")
    private String nombre; //

    @Column(name = "EMAIL", unique = true) // Cambiamos "USUARIO" por "EMAIL"
    private String email;

    @Column(name = "CONTRASENA")
    private String contrasena; // (Renombré 'password' a 'contrasena')

    @Column(name = "RFC", unique = true) // Campo NUEVO que necesitamos de POLICODELABS
    private String rfc;

    @Column(name = "estatus")
    private String estatus; // "DISPONIBLE", "DESCANSO", "FUERA_SERVICIO"

    @Column(name = "latitud_actual")
    private Double latitudActual;

    @Column(name = "longitud_actual")
    private Double longitudActual;

    // --- ¡LA MAGIA OCURRE AQUÍ! ---
    // 1. Conexión al Rol (para saber si es Gestor o Repartidor)
    @ManyToOne
    @JoinColumn(name = "ROL_ID", nullable = false)
    private Rol rol; // Usamos tu clase Rol.java

    // 2. Conexión al Negocio (para saber a qué empresa pertenece)
    @ManyToOne
    @JoinColumn(name = "ID_LICENCIA", nullable = false)
    private Negocio negocio; // (Necesitaremos crear la entidad 'Negocio.java')
}