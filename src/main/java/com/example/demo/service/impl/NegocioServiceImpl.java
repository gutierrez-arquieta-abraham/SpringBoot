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
    @Override
    public NegocioDto actualizarNegocio(Integer id, NegocioDto negocioDto) {
        // 1. Verificar si el negocio existe
        Negocio negocioExistente = negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio ID " + id + " no encontrado"));

        // 2. Aplicar los cambios
        negocioExistente.setNomEmp(negocioDto.getNomEmp());
        negocioExistente.setRfcEnc(negocioDto.getRfcEnc());

        // 3. Guardar (el método save() sabe que debe actualizar porque el ID ya existe)
        Negocio actualizado = negocioRepository.save(negocioExistente);
        return convertirADto(actualizado);
    }

    @Override
    public void eliminarNegocio(Integer id) {
        // 1. Usar la función de JPA para eliminar por ID
        negocioRepository.deleteById(id);
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