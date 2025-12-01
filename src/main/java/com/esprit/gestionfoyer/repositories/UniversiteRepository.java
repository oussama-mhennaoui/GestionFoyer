package com.esprit.gestionfoyer.repositories;

import com.esprit.gestionfoyer.entites.Universite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UniversiteRepository extends JpaRepository<Universite, Long> {
    Optional<Universite> findByNomUniversite(String nomUniversite);
}
