package com.esprit.gestionfoyer.repositories;

import com.esprit.gestionfoyer.entites.Chambre;
import com.esprit.gestionfoyer.entites.Reservation;
import com.esprit.gestionfoyer.entites.TypeChambre;
import com.esprit.gestionfoyer.entites.Universite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChambreRepository extends JpaRepository<Chambre, Long> {

    List<Chambre> findByNumeroChambreIn(List<Long> numeroChambre);
    List<Chambre> findByBlocIdBlocAndTypeC(long idBloc, TypeChambre typeC);
    List<Chambre> findByBlocFoyerUniversiteNomUniversiteAndTypeC(String nomUniversite, TypeChambre typeC);
    List<Chambre> findByBlocFoyerUniversiteNomUniversite(String nomUniversite);
    
}
