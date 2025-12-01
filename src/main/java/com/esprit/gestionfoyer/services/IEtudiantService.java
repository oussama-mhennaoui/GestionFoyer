package com.esprit.gestionfoyer.services;

import com.esprit.gestionfoyer.entites.Etudiant;

import java.util.List;

public interface IEtudiantService {
    List<Etudiant> retrieveAllEtudiants();
    List<Etudiant> addEtudiants(List<Etudiant> etudiants);
    Etudiant updateEtudiant(Etudiant e);
    Etudiant retrieveEtudiant(long idEtudiant);
    void removeEtudiant(long idEtudiant);
}
