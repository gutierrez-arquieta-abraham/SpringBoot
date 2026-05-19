package com.example.demo.controler;

import com.example.demo.dto.NegocioDto;
// Ya no necesitamos importar la entidad Negocio aquí. ¡Capa limpia!
import com.example.demo.service.NegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/negocios")
public class NegocioController {

    @Autowired
    private NegocioService negocioService;

    // --- ENDPOINT PARA VALIDAR ---
    @GetMapping("/validar/{codigo}")
    public ResponseEntity<NegocioDto> validarLicencia(@PathVariable String codigo) {
        return ResponseEntity.ok(negocioService.validarLicencia(codigo));
    }

    // --- AQUÍ ESTABA EL ERROR ---
    // Antes recibía @RequestBody Negocio negocio. Ahora recibe estrictamente el DTO.
    @PostMapping
    public ResponseEntity<NegocioDto> crearNegocio(@RequestBody NegocioDto negocioDto) {
        return ResponseEntity.ok(negocioService.crearNegocio(negocioDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NegocioDto> getNegocioById(@PathVariable Integer id) {
        return ResponseEntity.ok(negocioService.getNegocioById(id));
    }

    @GetMapping
    public ResponseEntity<List<NegocioDto>> getAllNegocios() {
        return ResponseEntity.ok(negocioService.getAllNegocios());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NegocioDto> actualizarNegocio(
            @PathVariable Integer id,
            @RequestBody NegocioDto negocioDto) {
        return ResponseEntity.ok(negocioService.actualizarNegocio(id, negocioDto));
    }

    @GetMapping("/propietario")
    public ResponseEntity<NegocioDto> getNegocioPorEmail(@RequestParam String email) {
        try {
            NegocioDto negocio = negocioService.obtenerNegocioPorEmailUsuario(email);
            return ResponseEntity.ok(negocio);
        } catch (Exception e) {
            // Si no se encuentra, devolvemos 404 para que Android sepa
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Integer> validarCodigoLicencia(@PathVariable String codigo) {
        Integer idNegocio = negocioService.obtenerIdPorCodigo(codigo);
        if (idNegocio != null) {
            return ResponseEntity.ok(idNegocio);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNegocio(@PathVariable Integer id) {
        negocioService.eliminarNegocio(id);
        return ResponseEntity.ok().build();
    }
}