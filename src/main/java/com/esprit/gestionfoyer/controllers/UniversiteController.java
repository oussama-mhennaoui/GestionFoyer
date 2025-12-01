package com.esprit.gestionfoyer.controllers;


import com.esprit.gestionfoyer.dto.UniversiteDTO;
import com.esprit.gestionfoyer.entites.Universite;
import com.esprit.gestionfoyer.services.IUniversiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/universites")
public class UniversiteController {

    @Autowired
    private IUniversiteService universiteService;

    @GetMapping
    public ResponseEntity<List<Universite>> getAllUniversities() {
        List<Universite> universites = universiteService.retrieveAllUniversities();
        return new ResponseEntity<>(universites, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Universite> getUniversiteById(@PathVariable long id) {
        Universite universite = universiteService.retrieveUniversite(id);
        return new ResponseEntity<>(universite, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Universite> createUniversite(@RequestBody Universite universite) {
        Universite savedUniversite = universiteService.addUniversite(universite);
        return new ResponseEntity<>(savedUniversite, HttpStatus.CREATED);
    }

    @PutMapping("/affecter-foyer")
    public ResponseEntity<Universite> affecterFoyerAUniversite(@RequestBody UniversiteDTO affectationDTO) {
        Universite universite = universiteService.affecterFoyerAUniversite(
                affectationDTO.getIdFoyer(),
                affectationDTO.getNomUniversite()
        );
        return new ResponseEntity<>(universite, HttpStatus.OK);
    }

    @PutMapping("/desaffecter-foyer/{idUniversite}")
    public ResponseEntity<Universite> desaffecterFoyerAUniversite(@PathVariable long idUniversite) {
        Universite universite = universiteService.desaffecterFoyerAUniversite(idUniversite);
        return new ResponseEntity<>(universite, HttpStatus.OK);
    }
}
