package com.esprit.gestionfoyer.services;

import com.esprit.gestionfoyer.entites.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface IReservationService {
    List<Reservation> retrieveAllReservation();
    Reservation updateReservation(Reservation res);
    Reservation retrieveReservation(String idReservation);

    Reservation ajouterReservation(long idChambre, long cinEtudiant);
    Reservation annulerReservation(long cinEtudiant);
    List<Reservation> getReservationParAnneeUniversitaireEtNomUniversite(LocalDate anneeUniversite, String nomUniversite);
}
