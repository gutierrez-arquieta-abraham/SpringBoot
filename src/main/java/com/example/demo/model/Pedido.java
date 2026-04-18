package com.example.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @NotBlank(message = "La descripción no puede estar vacía ni ser nula")
    @Column(name = "descripcion")
    private String descripcion;

    @NotBlank(message = "El destino es obligatorio")
    @Column(name = "destino")
    private String destino;

    // ESTE ES EL BUENO (EN_CURSO, ENTREGADO, ETC)
    @Column(name = "estado")
    private String estadoReal;

    @CreationTimestamp
    @Column(name = "fecha_hora_creacion", updatable = false)
    private LocalDateTime fechaHoraCreacion;

    @Column(name = "fecha_hora_recogida")
    private LocalDateTime fechaHoraRecogida;

    @Column(name = "fecha_hora_entrega")
    private LocalDateTime fechaHoraEntrega;

    @Column(name = "nombre_cliente")
    private String nombreCliente;

    @Column(name = "telefono_cliente")
    private String telefonoCliente;

    @Column(name = "latitud_destino")
    private Double latitudDestino;

    @Column(name = "longitud_destino")
    private Double longitudDestino;

    @Column(name = "minutos_transcurridos")
    private Long minutosTranscurridos;

    @Column(name = "kilometros_recorridos")
    private Double kilometrosRecorridos;

    // RELACIÓN CON NEGOCIO (Como lo tienes en tu último código)
    @NotNull(message = "El pedido debe estar asociado a un negocio")
    @ManyToOne
    @JoinColumn(name = "id_licencia", nullable = false)
    private Negocio negocio;

    // RELACIÓN CON REPARTIDOR
    @ManyToOne
    @JoinColumn(name = "id_usuario_rep")
    private Usuario repartidorAsignado;
}