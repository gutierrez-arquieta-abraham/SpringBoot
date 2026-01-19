package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    List<Usuario> findByRol_IdAndNegocio_IdLicencia(Integer rolId, Integer idLicencia);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByNegocio_IdLicenciaAndRol_Rol(Integer idLicencia, String rol);

}