package com.example.demo.repository;

import com.example.demo.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // <-- Importante

public interface RolRepository extends JpaRepository<Rol, Integer> {
    // Spring crea la consulta automáticamente basada en el nombre
    Optional<Rol> findByRol(String rol);
}