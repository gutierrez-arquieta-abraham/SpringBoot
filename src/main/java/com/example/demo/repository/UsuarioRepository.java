package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    List<Usuario> findByRol_IdAndNegocio_IdLicenciaAndActivoTrue(Integer rolId, Integer idLicencia);
    Optional<Usuario> findByEmailAndActivoTrue(String email);
    List<Usuario> findByNegocio_IdLicenciaAndRol_RolAndActivoTrue(Integer idLicencia, String rol);
}