package com.example.demo.service;

import com.example.demo.dto.NegocioDto;
import com.example.demo.model.Negocio;
import java.util.List;

public interface NegocioService {
    NegocioDto crearNegocio(Negocio negocio);
    NegocioDto getNegocioById(Integer id);
    List<NegocioDto> getAllNegocios();
    NegocioDto actualizarNegocio(Integer id, NegocioDto negocioDto);
    void eliminarNegocio(Integer id);

    // --- NUEVO ---
    NegocioDto validarLicencia(String codigoLicencia);
}