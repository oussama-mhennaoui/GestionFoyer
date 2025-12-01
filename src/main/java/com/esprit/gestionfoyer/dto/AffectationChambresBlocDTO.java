package com.esprit.gestionfoyer.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class AffectationChambresBlocDTO {
    private List<Long> numChambre;
    private long idBloc;
}
