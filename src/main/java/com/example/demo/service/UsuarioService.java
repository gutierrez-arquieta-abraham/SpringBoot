package com.example.demo.service;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.Usuario;

public interface UsuarioService {

    // Servicio para registrar (ya sea Gestor o Repartidor)
    UsuarioDto registrarUsuario(Usuario nuevoUsuario, String nombreRol);

    // Servicio para el login
    UsuarioDto login(LoginDto loginDto);
}