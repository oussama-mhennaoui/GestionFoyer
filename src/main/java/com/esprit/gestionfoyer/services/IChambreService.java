package com.esprit.gestionfoyer.services;

import com.esprit.gestionfoyer.entites.Chambre;
import com.esprit.gestionfoyer.entites.TypeChambre;

import java.util.List;
import java.util.Map;

public interface IChambreService {
    List<Chambre> retrieveAllChambres();
    Chambre addChambre(Chambre c);
    Chambre updateChambre(Chambre c);
    Chambre retrieveChambre(long idChambre);
    void removeChambre(long idChambre);

    List<Chambre> getChambresParNomUniversite(String nomUniversite);
    List<Chambre> getChambresParBlocEtType(long idBloc, TypeChambre typeC);
    List<Chambre> getChambresNonReserveParNomUniversiteEtTypeChambre(String nomUniversite, TypeChambre type);
    Map<String, List<Chambre>> getChambresNonReservePourToutesUniversites();
}
