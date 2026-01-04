package com.example.demo.service.impl;

import com.example.demo.dto.NegocioDto;
import com.example.demo.model.Negocio;
import com.example.demo.repository.NegocioRepository;
import com.example.demo.service.NegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NegocioServiceImpl implements NegocioService {

    @Autowired
    private NegocioRepository negocioRepository;

    @Override
    public NegocioDto crearNegocio(Negocio negocio) {
        // Si no trae código, generamos uno por defecto (Opcional)
        if (negocio.getCodigoLicencia() == null || negocio.getCodigoLicencia().isEmpty()) {
            // Genera algo como DIT-AB12-CD34-PL (Lógica simplificada)
            String randomCode = "DIT-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase() +
                    "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase() + "-PL";
            negocio.setCodigoLicencia(randomCode);
        }

        Negocio guardado = negocioRepository.save(negocio);
        return convertirADto(guardado);
    }

    // --- NUEVO MÉTODO PARA VALIDAR ---
    @Override
    public NegocioDto validarLicencia(String codigoLicencia) {
        Negocio negocio = negocioRepository.findByCodigoLicencia(codigoLicencia)
                .orElseThrow(() -> new RuntimeException("Licencia '" + codigoLicencia + "' no encontrada"));
        return convertirADto(negocio);
    }

    @Override
    public NegocioDto getNegocioById(Integer id) {
        Negocio negocio = negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));
        return convertirADto(negocio);
    }

    @Override
    public List<NegocioDto> getAllNegocios() {
        return negocioRepository.findAll()
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    public NegocioDto actualizarNegocio(Integer id, NegocioDto negocioDto) {
        Negocio negocioExistente = negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio ID " + id + " no encontrado"));

        negocioExistente.setNomEmp(negocioDto.getNomEmp());
        negocioExistente.setRfcEnc(negocioDto.getRfcEnc());
        // También permitimos actualizar el código si es necesario
        if (negocioDto.getCodigoLicencia() != null) {
            negocioExistente.setCodigoLicencia(negocioDto.getCodigoLicencia());
        }

        Negocio actualizado = negocioRepository.save(negocioExistente);
        return convertirADto(actualizado);
    }

    @Override
    public void eliminarNegocio(Integer id) {
        negocioRepository.deleteById(id);
    }

    private NegocioDto convertirADto(Negocio negocio) {
        NegocioDto dto = new NegocioDto();
        dto.setIdLicencia(negocio.getIdLicencia());
        dto.setNomEmp(negocio.getNomEmp());
        dto.setRfcEnc(negocio.getRfcEnc());
        dto.setCodigoLicencia(negocio.getCodigoLicencia()); // Mapear nuevo campo
        return dto;
    }
}