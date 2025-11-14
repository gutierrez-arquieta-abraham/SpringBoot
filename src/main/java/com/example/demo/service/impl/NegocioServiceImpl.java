package com.example.demo.service.impl;

import com.example.demo.dto.NegocioDto;
import com.example.demo.model.Negocio;
import com.example.demo.repository.NegocioRepository;
import com.example.demo.service.NegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NegocioServiceImpl implements NegocioService {

    @Autowired
    private NegocioRepository negocioRepository;

    @Override
    public NegocioDto crearNegocio(Negocio negocio) {
        // (Aquí puedes agregar validaciones, ej. que el RFC no exista)
        Negocio guardado = negocioRepository.save(negocio);
        return convertirADto(guardado);
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
                .map(this::convertirADto) // Llama al convertidor por cada ítem
                .collect(Collectors.toList());
    }

    // --- Convertidor ---
    private NegocioDto convertirADto(Negocio negocio) {
        NegocioDto dto = new NegocioDto();
        dto.setIdLicencia(negocio.getIdLicencia());
        dto.setNomEmp(negocio.getNomEmp());
        dto.setRfcEnc(negocio.getRfcEnc());
        return dto;
    }
}