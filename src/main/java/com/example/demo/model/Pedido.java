package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Num_ord")
    private Integer numOrd;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "destino")
    private String destino;

    // --- AGREGADO PARA QUE NO TE MARQUE ERROR EN EL SERVICE ---
    @Column(name = "estatus")
    private String estatus;
    // ---------------------------------------------------------

    // ESTE ES EL BUENO (EN_CURSO, ENTREGADO, ETC)
    @Column(name = "estado")
    private String estadoReal;

    @Column(name = "Fecha_de_entrega")
    private LocalDate fechaEntrega;

    @Column(name = "hora_de_entrega")
    private LocalTime horaEntrega;

    // RELACIÓN CON NEGOCIO (Como lo tienes en tu último código)
    @ManyToOne
    @JoinColumn(name = "id_licencia", nullable = false)
    private Negocio negocio;

    // RELACIÓN CON REPARTIDOR
    @ManyToOne
    @JoinColumn(name = "id_usuario_rep")
    private Usuario repartidorAsignado;
}