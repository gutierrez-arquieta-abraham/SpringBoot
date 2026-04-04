package com.example.demo.repository;

import com.example.demo.model.UbicacionPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Importante
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Importante

import java.util.List;

@Repository
public interface UbicacionPedidoRepository extends JpaRepository<UbicacionPedido, Long> {

    // --- AGREGAR ESTO ---

    // "Borrar donde el NumOrd del Pedido coincida"
    List<UbicacionPedido> findByPedido_NumOrdOrderByTimestampAsc(Integer numOrd);
}