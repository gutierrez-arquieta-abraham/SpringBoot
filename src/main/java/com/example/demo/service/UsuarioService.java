package com.example.demo.service;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.Usuario;

import java.util.List;

public interface UsuarioService {

    // --- Métodos de Autenticación y Registro ---
    UsuarioDto registrarUsuario(Usuario nuevoUsuario, String nombreRol);

    UsuarioDto login(LoginDto loginDto);

    // --- Métodos de CRUD y Gestión ---
    UsuarioDto getUsuarioById(Integer id);

    UsuarioDto actualizarUsuario(Integer id, UsuarioDto usuarioDto);

    void actualizarContrasena(Integer id, String nuevaContrasena);

    void eliminarUsuario(Integer id);

    // --- Métodos de Lógica de Negocio ---

    // Vincula un repartidor/gestor a un negocio específico
    void vincularNegocio(Integer idUsuario, Integer idLicencia);

    // --- ¡NUEVO! Cambia el estado del repartidor (Disponible, Descanso, etc.) ---
    void cambiarEstatus(Integer id, String nuevoEstatus);
    void actualizarUbicacionRepartidor(Integer idUsuario, Double lat, Double lon);
    List<UsuarioDto> obtenerRepartidoresPorNegocio(Integer idLicencia);
}