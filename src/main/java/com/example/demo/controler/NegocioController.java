package com.example.demo.controler;

import com.example.demo.dto.NegocioDto;
import com.example.demo.model.Negocio;
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

    // --- NUEVO ENDPOINT PARA VALIDAR ---
    // GET /api/negocios/validar/DIT-XXXX-XXXX-PL
    @GetMapping("/validar/{codigo}")
    public ResponseEntity<NegocioDto> validarLicencia(@PathVariable String codigo) {
        return ResponseEntity.ok(negocioService.validarLicencia(codigo));
    }

    @PostMapping
    public ResponseEntity<NegocioDto> crearNegocio(@RequestBody Negocio negocio) {
        return ResponseEntity.ok(negocioService.crearNegocio(negocio));
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
        // El servicio busca el VARCHAR "DIT-..." y nos da el ID (ej: 1)
        Integer idNegocio = negocioService.obtenerIdPorCodigo(codigo);

        if (idNegocio != null) {
            return ResponseEntity.ok(idNegocio); // 200 OK: Devuelve el ID 1
        } else {
            return ResponseEntity.notFound().build(); // 404: No existe ese código
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNegocio(@PathVariable Integer id) {
        negocioService.eliminarNegocio(id);
        return ResponseEntity.ok().build();
    }
}