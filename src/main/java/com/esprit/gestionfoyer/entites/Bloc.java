package com.esprit.gestionfoyer.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Bloc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBloc;

    private String nomBloc;
    private Long capaciteBloc;

    // Un bloc appartient à un seul foyer
    @ManyToOne
    @JoinColumn(name = "foyer_id")
    private Foyer foyer;

    // Un bloc contient plusieurs chambres
    @OneToMany(mappedBy = "bloc")
    private List<Chambre> chambres;
}