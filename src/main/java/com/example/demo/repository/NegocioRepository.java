package com.example.demo.repository;

import com.example.demo.model.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NegocioRepository extends JpaRepository<Negocio, Integer> {
}