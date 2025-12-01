package com.esprit.gestionfoyer.controllers;

import com.esprit.gestionfoyer.dto.AffectationChambresBlocDTO;
import com.esprit.gestionfoyer.entites.Bloc;
import com.esprit.gestionfoyer.services.IBlocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController

@RequestMapping("/blocs")
public class BlocController {
    @Autowired
    private IBlocService blocService;

    @GetMapping
    public ResponseEntity<List<Bloc>> getAllBlocs() {
        List<Bloc> blocs = blocService.retrieveAllBlocs();
        return new ResponseEntity<>(blocs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bloc> getBlocById(@PathVariable long id) {
        Bloc bloc = blocService.retrieveBloc(id);
        return new ResponseEntity<>(bloc, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Bloc> createBloc(@RequestBody Bloc bloc) {
        Bloc savedBloc = blocService.addBloc(bloc);
        return new ResponseEntity<>(savedBloc, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Bloc> updateBloc(@RequestBody Bloc bloc) {
        Bloc updatedBloc = blocService.updateBloc(bloc);
        return new ResponseEntity<>(updatedBloc, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBloc(@PathVariable long id) {
        blocService.removeBloc(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/affecter-chambres")
    public ResponseEntity<Bloc> affecterChambresABloc(@RequestBody AffectationChambresBlocDTO affectationDTO) {
        Bloc bloc = blocService.affecterChambresABloc(
                affectationDTO.getNumChambre(),
                affectationDTO.getIdBloc()
        );
        return new ResponseEntity<>(bloc, HttpStatus.OK);
    }
}
