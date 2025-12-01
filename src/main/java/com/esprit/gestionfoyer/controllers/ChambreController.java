package com.esprit.gestionfoyer.controllers;


import com.esprit.gestionfoyer.entites.Chambre;
import com.esprit.gestionfoyer.entites.TypeChambre;
import com.esprit.gestionfoyer.services.IChambreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chambres")
public class ChambreController {

    @Autowired
    private IChambreService chambreService;

    @GetMapping
    public ResponseEntity<List<Chambre>> getAllChambres() {
        List<Chambre> chambres = chambreService.retrieveAllChambres();
        return new ResponseEntity<>(chambres, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chambre> getChambreById(@PathVariable long id) {
        Chambre chambre = chambreService.retrieveChambre(id);
        return new ResponseEntity<>(chambre, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Chambre> createChambre(@RequestBody Chambre chambre) {
        Chambre savedChambre = chambreService.addChambre(chambre);
        return new ResponseEntity<>(savedChambre, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Chambre> updateChambre(@RequestBody Chambre chambre) {
        Chambre updatedChambre = chambreService.updateChambre(chambre);
        return new ResponseEntity<>(updatedChambre, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChambre(@PathVariable long id) {
        chambreService.removeChambre(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/universite/{nomUniversite}")
    public ResponseEntity<List<Chambre>> getChambresParNomUniversite(@PathVariable String nomUniversite) {
        List<Chambre> chambres = chambreService.getChambresParNomUniversite(nomUniversite);
        return new ResponseEntity<>(chambres, HttpStatus.OK);
    }

    @GetMapping("/bloc/{idBloc}/type/{typeC}")
    public ResponseEntity<List<Chambre>> getChambresParBlocEtType(@PathVariable long idBloc, @PathVariable TypeChambre typeC) {
        List<Chambre> chambres = chambreService.getChambresParBlocEtType(idBloc, typeC);
        return new ResponseEntity<>(chambres, HttpStatus.OK);
    }


    @GetMapping("/non-reserve/universite/{nomUniversite}/type/{type}")
    public ResponseEntity<List<Chambre>> getChambresNonReserveParNomUniversiteEtTypeChambre(@PathVariable String nomUniversite, @PathVariable TypeChambre type) {
        List<Chambre> chambres = chambreService.getChambresNonReserveParNomUniversiteEtTypeChambre(nomUniversite, type);
        return new ResponseEntity<>(chambres, HttpStatus.OK);
    }

    @GetMapping("/non-reserve/toutes-universites")
    public ResponseEntity<Map<String, List<Chambre>>> getChambresNonReservePourToutesUniversites() {
        Map<String, List<Chambre>> result = chambreService.getChambresNonReservePourToutesUniversites();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
