package com.esprit.gestionfoyer.repositories;

import com.esprit.gestionfoyer.entites.Chambre;
import com.esprit.gestionfoyer.entites.Etudiant;
import com.esprit.gestionfoyer.entites.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByChambreAndEstValideTrue(Chambre chambre);
    List<Reservation> findByEtudiantsAndEstValideTrue(Etudiant etudiant);
    List<Reservation> findByChambreBlocFoyerUniversiteNomUniversiteAndAnneeUniversitaire(String nomUniversite, LocalDate anneeUniversitaire);

    //List<Reservation> findByEstValideTrueAndAnneeUniversitaireBetween(LocalDate startDate, LocalDate endDate);
    // Get reservations for specific chambre
    List<Reservation> findByChambreIdChambreAndEstValideTrueAndAnneeUniversitaireBetween(Long idChambre, LocalDate startDate, LocalDate endDate);

}
