package com.esprit.gestionfoyer.services;

import com.esprit.gestionfoyer.entites.Foyer;
import com.esprit.gestionfoyer.entites.Universite;
import com.esprit.gestionfoyer.repositories.FoyerRepository;
import com.esprit.gestionfoyer.repositories.UniversiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UniversiteServiceImpl implements IUniversiteService {

    @Autowired
    private UniversiteRepository universiteRepository;
    @Autowired
    private FoyerRepository foyerRepository;

    @Override
    public List<Universite> retrieveAllUniversities() {
        return universiteRepository.findAll();
    }

    @Override
    public Universite addUniversite(Universite u) {
        return universiteRepository.save(u);
    }

    @Override
    public Universite updateUniversite(Universite u) {
        if (universiteRepository.existsById(u.getIdUniversite())) {
            return universiteRepository.save(u);
        }
        throw new RuntimeException("Universite not found with id: " + u.getIdUniversite());
    }

    @Override
    public Universite retrieveUniversite(long idUniversite) {
        return universiteRepository.findById(idUniversite)
                .orElseThrow(() -> new RuntimeException("Universite not found with id: " + idUniversite));
    }

    @Override
    public Universite affecterFoyerAUniversite(long idFoyer, String nomUniversite) {
        // Find the foyer by ID
        Foyer foyer = foyerRepository.findById(idFoyer).orElseThrow(() -> new RuntimeException("Foyer not found with id: " + idFoyer));
        // Find the university by name
        Universite universite = universiteRepository.findByNomUniversite(nomUniversite).orElseThrow(() -> new RuntimeException("Universite not found with name: " + nomUniversite));
        // Since Universite has the foreign key, set the foyer on the universite
        universite.setFoyer(foyer);
        // Save the universite
        return universiteRepository.save(universite);
    }

    @Override
    public Universite desaffecterFoyerAUniversite(long idUniversite) {
        // Find the university by ID
        Universite universite = universiteRepository.findById(idUniversite).orElseThrow(() -> new RuntimeException("Universite not found with id: " + idUniversite));
        // Check if the university has a foyer assigned
        if (universite.getFoyer() == null) {
            throw new RuntimeException("No foyer assigned to this university");
        }
        // Remove the foyer assignment by setting it to null
        universite.setFoyer(null);
        // Save the updated university
        return universiteRepository.save(universite);
    }
}
