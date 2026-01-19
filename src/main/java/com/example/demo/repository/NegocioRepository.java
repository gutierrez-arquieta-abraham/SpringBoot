package com.example.demo.repository;

import com.example.demo.model.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NegocioRepository extends JpaRepository<Negocio, Integer> {

    // Busca un negocio por su código "DIT-..."
    Optional<Negocio> findByCodigoLicencia(String codigoLicencia);
    Optional<Negocio> findByCodigoConexion(String codigoConexion);
    // Spring crea la query: SELECT * FROM negocio WHERE RFC_enc = ?
    Optional<Negocio> findByRfcEnc(String rfcEnc);

}