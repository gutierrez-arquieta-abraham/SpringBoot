package com.example.demo.repository;

import com.example.demo.model.UbicacionPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<UbicacionPedido, Long> {

    // Ruta de un pedido específico ordenada del más reciente al más antiguo
    List<UbicacionPedido> findByPedido_NumOrdOrderByTimestampDesc(Integer numOrd);

    // Ruta de un pedido específico ordenada del más antiguo al más reciente (Rescatado del repo eliminado)
    List<UbicacionPedido> findByPedido_NumOrdOrderByTimestampAsc(Integer numOrd);

    // Radar: Última ubicación de cada pedido activo
    @Query("SELECT u FROM UbicacionPedido u WHERE u.timestamp IN " +
            "(SELECT MAX(u2.timestamp) FROM UbicacionPedido u2 GROUP BY u2.pedido.numOrd)")
    List<UbicacionPedido> findUltimasUbicaciones();
}