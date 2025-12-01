package com.esprit.gestionfoyer.repositories;

import com.esprit.gestionfoyer.entites.Foyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FoyerRepository extends JpaRepository<Foyer, Long> {

}
