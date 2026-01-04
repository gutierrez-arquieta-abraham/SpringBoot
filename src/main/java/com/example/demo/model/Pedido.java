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
@Table(name = "pedidos")
public class Pedido {

    @Id
    @Column(name = "num_ord")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numOrd;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "destino")
    private String destino;

    // --- ESTA ES LA COLUMNA QUE FALTABA ---
    @Column(name = "estado")
    private String estado;
    // --------------------------------------

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_de_entrega")
    private Date fechaDeEntrega;

    @Temporal(TemporalType.TIME)
    @Column(name = "hora_de_entrega")
    private Date horaDeEntrega;

    @ManyToOne
    @JoinColumn(name = "id_licencia", nullable = false)
    private Negocio negocio;

    @ManyToOne
    @JoinColumn(name = "id_usuario_rep")
    private Usuario repartidorAsignado;
}