package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Spring crea la consulta: "SELECT * FROM Usuario WHERE EMAIL = ?"
    Optional<Usuario> findByEmail(String email);
}