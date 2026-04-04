package com.example.demo.repository;

import com.example.demo.model.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NegocioRepository extends JpaRepository<Negocio, Integer> {

    Optional<Negocio> findByCodigoLicenciaAndActivoTrue(String codigoLicencia);

    Optional<Negocio> findByCodigoConexionAndActivoTrue(String codigoConexion);

    Optional<Negocio> findByRfcEncAndActivoTrue(String rfcEnc);

    // NUEVO: Para reemplazar el findAll() y no mostrar negocios borrados
    List<Negocio> findByActivoTrue();
}