package com.esprit.gestionfoyer.controllers;

import com.esprit.gestionfoyer.dto.AjouterReservationDTO;
import com.esprit.gestionfoyer.dto.AnnulerReservationDTO;
import com.esprit.gestionfoyer.entites.Reservation;
import com.esprit.gestionfoyer.services.IReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    private IReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.retrieveAllReservation();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable String id) {
        Reservation reservation = reservationService.retrieveReservation(id);
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Reservation> updateReservation(@RequestBody Reservation reservation) {
        Reservation updatedReservation = reservationService.updateReservation(reservation);
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Reservation> ajouterReservation(@RequestBody AjouterReservationDTO request) {
        Reservation reservation = reservationService.ajouterReservation(
                request.getIdChambre(),
                request.getCinEtudiant()
        );
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    @PutMapping("/annuler")
    public ResponseEntity<Reservation> annulerReservation(@RequestBody AnnulerReservationDTO request) {
        Reservation reservation = reservationService.annulerReservation(request.getCinEtudiant());
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }


    @GetMapping("/universite/{nomUniversite}/annee/{anneeUniversite}")
    public ResponseEntity<List<Reservation>> getReservationParAnneeUniversitaireEtNomUniversite(@PathVariable String nomUniversite, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate anneeUniversite) {

        List<Reservation> reservations = reservationService.getReservationParAnneeUniversitaireEtNomUniversite(anneeUniversite, nomUniversite);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

}
