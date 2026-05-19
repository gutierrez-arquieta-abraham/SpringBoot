package com.example.demo.service;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UsuarioDto;
import java.util.List;

public interface UsuarioService {
    // Autenticación y Registro estricto con DTO
    UsuarioDto registrarUsuario(UsuarioDto nuevoUsuarioDto, String nombreRol);
    UsuarioDto login(LoginDto loginDto);

    // CRUD
    UsuarioDto getUsuarioById(Integer id);
    UsuarioDto actualizarUsuario(Integer id, UsuarioDto usuarioDto);
    void eliminarUsuario(Integer id);

    // Gestión de credenciales
    void actualizarContrasena(Integer id, String nuevaContrasena);
    void recuperarContrasenaPorEmail(String email, String nuevaContrasena);

    // Lógica de Negocio
    void vincularNegocio(Integer idUsuario, Integer idLicencia);
    UsuarioDto vincularRepartidorPorCodigo(Integer idUsuario, String codigoConexion);
    UsuarioDto actualizarEstatus(Integer idUsuario, String nuevoEstatus);
    void actualizarUbicacion(Integer idRepartidor, Double latitud, Double longitud);

    List<UsuarioDto> obtenerRepartidoresPorNegocio(Integer idLicencia);
}