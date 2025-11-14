package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp; // ¡Importante!
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Ubicacion_Pedido")
public class UbicacionPedido {

    @Id
    @Column(name = "ID_ubicacion")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUbicacion; // Usamos Long porque puede crecer muchísimo

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    // ¡Automático! Esto le dice a la BD que ponga la fecha y hora actual al crear.
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp", updatable = false)
    private Date timestamp;

    // --- Relación con Pedido ---
    @ManyToOne
    @JoinColumn(name = "Num_ord", nullable = false)
    private Pedido pedido;

    // Constructor simple para la lógica de negocio
    public UbicacionPedido(Pedido pedido, Double latitud, Double longitud) {
        this.pedido = pedido;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}