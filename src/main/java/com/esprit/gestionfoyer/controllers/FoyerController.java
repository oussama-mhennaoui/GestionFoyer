package com.esprit.gestionfoyer.controllers;


import com.esprit.gestionfoyer.dto.AjouterFoyerEtAffecterAUniversiteDTO;
import com.esprit.gestionfoyer.entites.Foyer;
import com.esprit.gestionfoyer.services.IFoyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foyer")
public class FoyerController {
    @Autowired
    private IFoyerService foyerService;
    @GetMapping
    public ResponseEntity<List<Foyer>> getAllFoyers() {
        List<Foyer> foyers = foyerService.retrieveAllFoyers();
        return new ResponseEntity<>(foyers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Foyer> getFoyerById(@PathVariable long id) {
        Foyer foyer = foyerService.retrieveFoyer(id);
        return new ResponseEntity<>(foyer, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Foyer> createFoyer(@RequestBody Foyer foyer) {
        Foyer savedFoyer = foyerService.addFoyer(foyer);
        return new ResponseEntity<>(savedFoyer, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Foyer> updateFoyer(@RequestBody Foyer foyer) {
        Foyer updatedFoyer = foyerService.updateFoyer(foyer);
        return new ResponseEntity<>(updatedFoyer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoyer(@PathVariable long id) {
        foyerService.removeFoyer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping("/ajouter-foyer-etAffecter-aUniversite")
    public ResponseEntity<Foyer> ajouterFoyerEtAffecterAUniversite(@RequestBody AjouterFoyerEtAffecterAUniversiteDTO request) {
        Foyer foyer = foyerService.ajouterFoyerEtAffecterAUniversite(
                request.getFoyer(),
                request.getIdUniversite()
        );
        return new ResponseEntity<>(foyer, HttpStatus.CREATED);
    }
}
