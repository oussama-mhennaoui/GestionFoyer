package com.esprit.gestionfoyer.services;

import com.esprit.gestionfoyer.entites.Etudiant;
import com.esprit.gestionfoyer.repositories.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtudiantServiceImpl implements IEtudiantService{


    @Autowired
    private EtudiantRepository etudiantRepository;

    @Override
    public List<Etudiant> retrieveAllEtudiants() {
        return etudiantRepository.findAll();
    }

    @Override

    public List<Etudiant> addEtudiants(List<Etudiant> etudiants) {
        // Save all students in batch
        return etudiantRepository.saveAll(etudiants);
    }

    @Override
    public Etudiant updateEtudiant(Etudiant e) {
        // Check if student exists before updating
        if (etudiantRepository.existsById(e.getIdEtudiant())) {
            return etudiantRepository.save(e);
        }
        throw new RuntimeException("Etudiant not found with id: " + e.getIdEtudiant());
    }

    @Override
    public Etudiant retrieveEtudiant(long idEtudiant) {
        return etudiantRepository.findById(idEtudiant)
                .orElseThrow(() -> new RuntimeException("Etudiant not found with id: " + idEtudiant));
    }

    @Override
    public void removeEtudiant(long idEtudiant) {
        if (etudiantRepository.existsById(idEtudiant)) {
            etudiantRepository.deleteById(idEtudiant);
        } else {
            throw new RuntimeException("Etudiant not found with id: " + idEtudiant);
        }
    }
}
