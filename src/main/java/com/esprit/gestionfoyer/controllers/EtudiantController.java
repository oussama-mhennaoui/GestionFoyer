package com.esprit.gestionfoyer.controllers;

import com.esprit.gestionfoyer.entites.Etudiant;
import com.esprit.gestionfoyer.services.IEtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/etudiants")
public class EtudiantController {

        @Autowired
        private IEtudiantService etudiantService;

        @GetMapping
        public ResponseEntity<List<Etudiant>> getAllEtudiants() {
            List<Etudiant> etudiants = etudiantService.retrieveAllEtudiants();
            return new ResponseEntity<>(etudiants, HttpStatus.OK);
        }

        @PostMapping("/batch")
        public ResponseEntity<List<Etudiant>> createEtudiants(@RequestBody List<Etudiant> etudiants) {
            List<Etudiant> savedEtudiants = etudiantService.addEtudiants(etudiants);
            return new ResponseEntity<>(savedEtudiants, HttpStatus.CREATED);
        }

        @PostMapping
        public ResponseEntity<Etudiant> createEtudiant(@RequestBody Etudiant etudiant) {
            // For single student creation
            Etudiant savedEtudiant = etudiantService.addEtudiants(List.of(etudiant)).get(0);
            return new ResponseEntity<>(savedEtudiant, HttpStatus.CREATED);
        }

        @GetMapping("/{id}")
        public ResponseEntity<Etudiant> getEtudiantById(@PathVariable long id) {
            Etudiant etudiant = etudiantService.retrieveEtudiant(id);
            return new ResponseEntity<>(etudiant, HttpStatus.OK);
        }

        @PutMapping
        public ResponseEntity<Etudiant> updateEtudiant(@RequestBody Etudiant etudiant) {
            Etudiant updatedEtudiant = etudiantService.updateEtudiant(etudiant);
            return new ResponseEntity<>(updatedEtudiant, HttpStatus.OK);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteEtudiant(@PathVariable long id) {
            etudiantService.removeEtudiant(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
}
