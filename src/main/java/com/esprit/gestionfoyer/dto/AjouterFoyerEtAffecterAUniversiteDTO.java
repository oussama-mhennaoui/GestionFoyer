package com.esprit.gestionfoyer.dto;

import com.esprit.gestionfoyer.entites.Foyer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AjouterFoyerEtAffecterAUniversiteDTO {
    private Foyer foyer;
    private long idUniversite;
}
