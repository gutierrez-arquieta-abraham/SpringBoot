package com.example.demo.repository;

import com.example.demo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // Spring crea el SQL: "WHERE repartidorAsignado.id = ?"
    List<Pedido> findByRepartidorAsignado_Id(Integer idUsuario);

    // Spring crea el SQL: "WHERE negocio.idLicencia = ?"
    List<Pedido> findByNegocio_IdLicencia(Integer idLicencia);
}