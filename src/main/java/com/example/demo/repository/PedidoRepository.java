package com.example.demo.repository;

import com.example.demo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // Spring crea el SQL: "WHERE repartidorAsignado.id = ?"
    List<Pedido> findByRepartidorAsignadoIdAndEstadoReal(Integer idRepartidor, String estadoReal);
    List<Pedido> findByRepartidorAsignado_Id(Integer idUsuario);
    // Busca pedidos que ya terminaron
    @Query("SELECT p FROM Pedido p WHERE p.repartidorAsignado.id = :idRepartidor AND p.estadoReal = 'ENTREGADO'")
    List<Pedido> findHistorialPorRepartidor(@Param("idRepartidor") Integer idRepartidor);
    // Busca pedidos ENTREGADOS de un negocio en específico
    @Query("SELECT p FROM Pedido p WHERE p.negocio.idLicencia = :idLicencia AND p.estadoReal = 'ENTREGADO'")
    List<Pedido> findHistorialPorNegocio(@Param("idLicencia") Integer idLicencia);

    // Spring crea el SQL: "WHERE negocio.idLicencia = ?"
    List<Pedido> findByNegocio_IdLicencia(Integer idLicencia);
    @Query(value = "SELECT * FROM pedidos WHERE ID_USUARIO_REP = :idRepartidor", nativeQuery = true)
    List<Pedido> encontrarPedidosDeRepartidor(@Param("idRepartidor") Integer idRepartidor);


}