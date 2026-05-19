package com.example.demo.service;

import com.example.demo.dto.RolDto;

public interface RolService {
    // REGLA: Nada de entidades puras. Todo transita como DTO.
    RolDto crearRol(RolDto rolDto);
    RolDto actualizarRol(Integer id, RolDto rolDetalles);
    void eliminarRol(Integer id);
}