package com.esprit.gestionfoyer.services;

import com.esprit.gestionfoyer.entites.Bloc;
import com.esprit.gestionfoyer.entites.Foyer;
import com.esprit.gestionfoyer.entites.Universite;
import com.esprit.gestionfoyer.repositories.BlocRepository;
import com.esprit.gestionfoyer.repositories.FoyerRepository;
import com.esprit.gestionfoyer.repositories.UniversiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoyerServiceImpl implements IFoyerService{
    @Autowired
    private FoyerRepository foyerRepository;

    @Autowired
    private BlocRepository blocRepository;

    @Autowired
    private UniversiteRepository universiteRepository;

    @Override
    public List<Foyer> retrieveAllFoyers() {
        return foyerRepository.findAll();
    }

    @Override
    public Foyer addFoyer(Foyer f) {
        return foyerRepository.save(f);
    }

    @Override
    public Foyer updateFoyer(Foyer f) {
        // Check if foyer exists before updating
        if (foyerRepository.existsById(f.getIdFoyer())) {
            return foyerRepository.save(f);
        }
        throw new RuntimeException("Foyer not found with id: " + f.getIdFoyer());
    }

    @Override
    public Foyer retrieveFoyer(long idFoyer) {
        return foyerRepository.findById(idFoyer)
                .orElseThrow(() -> new RuntimeException("Foyer not found with id: " + idFoyer));
    }

    @Override
    public void removeFoyer(long idFoyer) {
        if (foyerRepository.existsById(idFoyer)) {
            foyerRepository.deleteById(idFoyer);
        } else {
            throw new RuntimeException("Foyer not found with id: " + idFoyer);
        }
    }

    @Override
    public Foyer ajouterFoyerEtAffecterAUniversite(Foyer foyer, long idUniversite) {
        // 1. Find the university
        Universite universite = universiteRepository.findById(idUniversite).orElseThrow(() -> new RuntimeException("Universite not found with id: " + idUniversite));
        // 2. Check if university already has a foyer
        if (universite.getFoyer() != null) {
            throw new RuntimeException("Universite already has a foyer assigned");
        }
        // 3. For each bloc in the foyer, set the foyer reference
        if (foyer.getBlocs() != null) {
            for (Bloc bloc : foyer.getBlocs()) {
                bloc.setFoyer(foyer);
            }
        }
        // 4. Save the foyer  
        Foyer savedFoyer = foyerRepository.save(foyer);
        // 5. Assign the foyer to the university
        universite.setFoyer(savedFoyer);
        universiteRepository.save(universite);
        return savedFoyer;
    }


}
