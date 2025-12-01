package com.esprit.gestionfoyer.services;

import com.esprit.gestionfoyer.entites.Bloc;

import java.util.List;

public interface IBlocService {
    List<Bloc> retrieveAllBlocs();
    Bloc addBloc(Bloc bloc);
    Bloc updateBloc(Bloc bloc);
    Bloc retrieveBloc(long idBloc);
    void removeBloc(long idBloc);


    Bloc affecterChambresABloc(List<Long> numChambre, long idBloc);
}
