package com.example.demo.service.impl;

import com.example.demo.model.Rol;
import com.example.demo.repository.RolRepository;
import com.example.demo.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository; // Inyecta el Repositorio

    @Override
    public Rol crearRol(Rol rol) {
        // (Aquí podríamos validar que no esté duplicado, pero .save() es suficiente)
        return rolRepository.save(rol);
    }
}