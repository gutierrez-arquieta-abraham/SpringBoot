package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Pedidos")
public class Pedido {

    @Id
    @Column(name = "Num_ord")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numOrd;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "Estado")
    private String estado;

    @Column(name = "destino")
    private String destino;

    @Temporal(TemporalType.DATE)
    @Column(name = "Fecha_de_entrega")
    private Date fechaDeEntrega;

    @Temporal(TemporalType.TIME)
    @Column(name = "hora_de_entrega")
    private Date horaDeEntrega;

    // Conexión al Negocio que creó el pedido
    @ManyToOne
    @JoinColumn(name = "ID_licencia", nullable = false)
    private Negocio negocio;

    // Conexión al Repartidor (Usuario) asignado
    @ManyToOne
    @JoinColumn(name = "ID_USUARIO_REP") // Puede ser null si no está asignado
    private Usuario repartidorAsignado;
}