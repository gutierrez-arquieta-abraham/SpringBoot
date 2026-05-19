package com.example.demo.repository;

import com.example.demo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByRepartidorAsignadoIdAndEstadoReal(Integer idRepartidor, String estadoReal);

    List<Pedido> findByNegocio_IdLicencia(Integer idLicencia);

    @Query("SELECT p FROM Pedido p WHERE p.repartidorAsignado.id = :idRepartidor AND p.estadoReal = 'ENTREGADO'")
    List<Pedido> findHistorialPorRepartidor(@Param("idRepartidor") Integer idRepartidor);

    @Query("SELECT p FROM Pedido p WHERE p.negocio.idLicencia = :idLicencia AND p.estadoReal = 'ENTREGADO'")
    List<Pedido> findHistorialPorNegocio(@Param("idLicencia") Integer idLicencia);
}