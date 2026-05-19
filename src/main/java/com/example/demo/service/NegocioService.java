package com.example.demo.service;

import com.example.demo.dto.NegocioDto;
import java.util.List;

public interface NegocioService {
    // REGLA DE ORO: Recibes DTO, devuelves DTO. Nada de entidades crudas.
    NegocioDto crearNegocio(NegocioDto negocioDto);

    NegocioDto getNegocioById(Integer id);
    Integer obtenerIdPorCodigo(String codigo);
    NegocioDto obtenerNegocioPorEmailUsuario(String email);
    List<NegocioDto> getAllNegocios();
    NegocioDto actualizarNegocio(Integer id, NegocioDto negocioDto);
    void eliminarNegocio(Integer id);
    NegocioDto validarLicencia(String codigoLicencia);
}