package com.example.demo.service;

import com.example.demo.dto.NegocioDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.Negocio;
import java.util.List;

public interface NegocioService {

    NegocioDto crearNegocio(Negocio negocio);

    NegocioDto getNegocioById(Integer id);

    Integer obtenerIdPorCodigo(String codigo);

    // Este se queda igual, devuelve el objeto completo
    NegocioDto obtenerNegocioPorEmailUsuario(String email);

    List<NegocioDto> getAllNegocios();

    NegocioDto actualizarNegocio(Integer id, NegocioDto negocioDto);

    void eliminarNegocio(Integer id);

    // Este método antiguo puedes mantenerlo o borrarlo si ya no lo usas
    NegocioDto validarLicencia(String codigoLicencia);
}