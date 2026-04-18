package com.example.demo.controler;

import com.example.demo.dto.DashboardNegocioDto;
import com.example.demo.service.PedidoService; // O EstadisticasService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/negocio/{idLicencia}")
    public ResponseEntity<DashboardNegocioDto> obtenerDashboard(@PathVariable Integer idLicencia) {
        try {
            DashboardNegocioDto dashboard = pedidoService.generarDashboardAnalitico(idLicencia);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}