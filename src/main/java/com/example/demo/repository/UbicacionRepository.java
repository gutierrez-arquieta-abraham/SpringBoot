package com.example.demo.repository;

import com.example.demo.model.UbicacionPedido; // (Asegúrate de tener el Modelo)
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UbicacionRepository extends JpaRepository<UbicacionPedido, Long> {

    // Busca la ruta más reciente de un pedido específico.
    List<UbicacionPedido> findByPedido_NumOrdOrderByTimestampDesc(Integer numOrd);
}