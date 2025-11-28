package com.example.demo.repository;

import com.example.demo.model.UbicacionPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UbicacionRepository extends JpaRepository<UbicacionPedido, Long> {

    // --- NUEVA CONSULTA MAESTRA ---
    @Query("SELECT u FROM UbicacionPedido u " +
            "WHERE u.timestamp IN " +
            "(SELECT MAX(u2.timestamp) FROM UbicacionPedido u2 GROUP BY u2.pedido.numOrd)")
    List<UbicacionPedido> findUltimasUbicaciones();
    List<UbicacionPedido> findByPedido_NumOrdOrderByTimestampDesc(Integer numOrd);
}