package com.example.demo.service.impl;

import com.example.demo.dto.RolDto;
import com.example.demo.model.Rol;
import com.example.demo.repository.RolRepository;
import com.example.demo.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public RolDto crearRol(RolDto rolDto) {
        // Nada de dejarle el trabajo sucio a la base de datos. Validamos nosotros.
        Optional<Rol> rolExistente = rolRepository.findByRol(rolDto.getRol().toUpperCase());
        if (rolExistente.isPresent()) {
            throw new RuntimeException("Error: El rol '" + rolDto.getRol() + "' ya existe en el sistema.");
        }

        Rol nuevoRol = new Rol();
        // Forzamos a mayúsculas para mantener consistencia (ej. "GESTOR", "REPARTIDOR")
        nuevoRol.setRol(rolDto.getRol().toUpperCase());

        return convertirADto(rolRepository.save(nuevoRol));
    }

    @Override
    public RolDto actualizarRol(Integer id, RolDto rolDetalles) {
        Rol rolExistente = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol ID " + id + " no encontrado"));

        // Validar que el nuevo nombre no choque con otro rol existente
        Optional<Rol> rolDuplicado = rolRepository.findByRol(rolDetalles.getRol().toUpperCase());
        if (rolDuplicado.isPresent() && !rolDuplicado.get().getId().equals(id)) {
            throw new RuntimeException("Error: El nombre de rol '" + rolDetalles.getRol() + "' ya está ocupado.");
        }

        rolExistente.setRol(rolDetalles.getRol().toUpperCase());
        return convertirADto(rolRepository.save(rolExistente));
    }

    @Override
    public void eliminarRol(Integer id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol ID " + id + " no encontrado"));

        // OJO AQUÍ: Si este rol ya está asignado a un usuario y lo borras, vas a romper las llaves foráneas.
        // Lo ideal en sistemas reales es hacer un "Soft Delete" (Activo/Inactivo), pero por ahora lo dejamos así.
        rolRepository.delete(rol);
    }

    // --- CONVERSOR PRIVADO ---
    private RolDto convertirADto(Rol rol) {
        return RolDto.builder()
                .rol(rol.getRol())
                // Si en tu RolDto agregas el ID en el futuro, lo mapeas aquí.
                .build();
    }
}