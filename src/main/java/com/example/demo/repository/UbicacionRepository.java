package com.example.demo.repository;

import com.example.demo.model.UbicacionPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UbicacionRepository extends JpaRepository<UbicacionPedido, Long> {

    // Ruta de un pedido específico
    List<UbicacionPedido> findByPedido_NumOrdOrderByTimestampDesc(Integer numOrd);

    // Radar: Última ubicación de cada pedido
    @Query("SELECT u FROM UbicacionPedido u WHERE u.timestamp IN " +
            "(SELECT MAX(u2.timestamp) FROM UbicacionPedido u2 GROUP BY u2.pedido.numOrd)")
    List<UbicacionPedido> findUltimasUbicaciones();
}