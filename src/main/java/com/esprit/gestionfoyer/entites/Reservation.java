package com.esprit.gestionfoyer.entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor

public class Reservation {
    @Id
    private String idReservation;  // ex: 12-2024-12345678

    @Column(nullable = false)
    private LocalDate anneeUniversitaire;

    private boolean estValide;

    // Réservation appartient à UNE seule chambre
    @ManyToOne
    @JoinColumn(name = "chambre_id")
    private Chambre chambre;

    // Plusieurs étudiants peuvent être dans la même réservation (ex: chambre double/triple)
    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "reservation_etudiant",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "etudiant_id")
    )
    private List<Etudiant> etudiants = new ArrayList<>();
}