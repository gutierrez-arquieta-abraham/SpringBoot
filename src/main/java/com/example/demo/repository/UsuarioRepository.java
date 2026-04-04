package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Solo trae repartidores de un negocio que sigan activos
    List<Usuario> findByRol_IdAndNegocio_IdLicenciaAndActivoTrue(Integer rolId, Integer idLicencia);

    // Evita que un usuario desactivado inicie sesión
    Optional<Usuario> findByEmailAndActivoTrue(String email);

    // Para las asignaciones automáticas, solo busca repartidores activos
    List<Usuario> findByNegocio_IdLicenciaAndRol_RolAndActivoTrue(Integer idLicencia, String rol);
}