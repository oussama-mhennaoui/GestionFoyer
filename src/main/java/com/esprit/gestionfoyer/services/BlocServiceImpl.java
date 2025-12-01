package com.esprit.gestionfoyer.services;

import com.esprit.gestionfoyer.entites.Bloc;
import com.esprit.gestionfoyer.entites.Chambre;
import com.esprit.gestionfoyer.repositories.BlocRepository;
import com.esprit.gestionfoyer.repositories.ChambreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlocServiceImpl implements IBlocService {

    @Autowired
    private BlocRepository blocRepository;
    @Autowired
    private ChambreRepository chambreRepository;

    @Override
    public List<Bloc> retrieveAllBlocs() {
        return blocRepository.findAll();
    }

    @Override
    public Bloc addBloc(Bloc bloc) {
        return blocRepository.save(bloc);
    }

    @Override
    public Bloc updateBloc(Bloc bloc) {
        // Check if bloc exists before updating
        if (blocRepository.existsById(bloc.getIdBloc())) {
            return blocRepository.save(bloc);
        }
        throw new RuntimeException("Bloc not found with id: " + bloc.getIdBloc());
    }

    @Override
    public Bloc retrieveBloc(long idBloc) {
        return blocRepository.findById(idBloc)
                .orElseThrow(() -> new RuntimeException("Bloc not found with id: " + idBloc));
    }

    @Override
    public void removeBloc(long idBloc) {
        if (blocRepository.existsById(idBloc)) {
            blocRepository.deleteById(idBloc);
        } else {
            throw new RuntimeException("Bloc not found with id: " + idBloc);
        }
    }

    @Override
    public Bloc affecterChambresABloc(List<Long> numChambre, long idBloc) {
        // Find the bloc by ID
        Bloc bloc = blocRepository.findById(idBloc).orElseThrow(() -> new RuntimeException("Bloc not found with id: " + idBloc));
        // Find all chambres by their numbers
        List<Chambre> chambres = chambreRepository.findByNumeroChambreIn(numChambre);
        // Check if all requested chambres were found
        if (chambres.size() != numChambre.size()) {
            throw new RuntimeException("Some chambres were not found");
        }
        // Assign the bloc to each chambre
        for (Chambre chambre : chambres) {
            chambre.setBloc(bloc);
        }
        // Save all updated chambres
        chambreRepository.saveAll(chambres);

        return bloc;
    }
}
