package com.esprit.gestionfoyer.repositories;

import com.esprit.gestionfoyer.entites.Bloc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BlocRepository extends JpaRepository<Bloc, Long> {

}
