package com.esprit.gestionfoyer.services;

import com.esprit.gestionfoyer.entites.*;
import com.esprit.gestionfoyer.repositories.ChambreRepository;
import com.esprit.gestionfoyer.repositories.ReservationRepository;
import com.esprit.gestionfoyer.repositories.UniversiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChambreServiceImpl implements IChambreService {
    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private UniversiteRepository universiteRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public List<Chambre> retrieveAllChambres() {
        return chambreRepository.findAll();
    }

    @Override
    public Chambre addChambre(Chambre c) {
        return chambreRepository.save(c);
    }

    @Override
    public Chambre updateChambre(Chambre c) {
        // Check if chambre exists before updating
        if (chambreRepository.existsById(c.getIdChambre())) {
            return chambreRepository.save(c);
        }
        throw new RuntimeException("Chambre not found with id: " + c.getIdChambre());
    }

    @Override
    public Chambre retrieveChambre(long idChambre) {
        return chambreRepository.findById(idChambre)
                .orElseThrow(() -> new RuntimeException("Chambre not found with id: " + idChambre));
    }

    @Override
    public void removeChambre(long idChambre) {
        if (chambreRepository.existsById(idChambre)) {
            chambreRepository.deleteById(idChambre);
        } else {
            throw new RuntimeException("Chambre not found with id: " + idChambre);
        }
    }

    @Override
    public List<Chambre> getChambresParNomUniversite(String nomUniversite) {
        // 1. Find the university by name
        // Use universiteRepository, not chambreRepository
        Universite universite = universiteRepository.findByNomUniversite(nomUniversite).orElseThrow(() -> new RuntimeException("Universite not found with name: " + nomUniversite));
        // 2. Check if university has a foyer
        if (universite.getFoyer() == null) {
            throw new RuntimeException("Universite " + nomUniversite + " doesn't have a foyer assigned");
        }
        Foyer foyer = universite.getFoyer();
        // 3. Check if foyer has blocs
        if (foyer.getBlocs() == null || foyer.getBlocs().isEmpty()) {
            return new ArrayList<>();
        }
        // 4. Collect all chambres from all blocs
        List<Chambre> allChambres = new ArrayList<>();
        for (Bloc bloc : foyer.getBlocs()) {
            if (bloc.getChambres() != null && !bloc.getChambres().isEmpty()) {
                allChambres.addAll(bloc.getChambres());
            }
        }

        return allChambres;
    }


    @Override
    public List<Chambre> getChambresParBlocEtType(long idBloc, TypeChambre typeC) {
        return chambreRepository.findByBlocIdBlocAndTypeC(idBloc, typeC);
    }

    @Override
    public List<Chambre> getChambresNonReserveParNomUniversiteEtTypeChambre(
            String nomUniversite, TypeChambre type) {

        // 1. Get current academic year dates (assuming academic year starts in September)
        LocalDate currentYearStart = getCurrentAcademicYearStart();
        LocalDate currentYearEnd = getCurrentAcademicYearEnd();

        // 2. Get all chambres for this university and type
        List<Chambre> allChambres = chambreRepository.findByBlocFoyerUniversiteNomUniversiteAndTypeC(nomUniversite, type);

        // 3. Filter out chambres that have valid reservations in current year
        List<Chambre> availableChambres = allChambres.stream().filter(chambre -> !hasValidReservationInCurrentYear(chambre, currentYearStart, currentYearEnd)).collect(Collectors.toList());

        return availableChambres;
    }
    private boolean hasValidReservationInCurrentYear(Chambre chambre, LocalDate startDate, LocalDate endDate) {
        List<Reservation> reservations = reservationRepository.findByChambreIdChambreAndEstValideTrueAndAnneeUniversitaireBetween(
                chambre.getIdChambre(), startDate, endDate);
        return !reservations.isEmpty();
    }
    private LocalDate getCurrentAcademicYearStart() {
        // Academic year typically starts in September
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        // If we're after September, academic year started this year
        // If we're before September, academic year started last year
        if (currentMonth >= 9) {
            return LocalDate.of(currentYear, 9, 1);
        } else {
            return LocalDate.of(currentYear - 1, 9, 1);
        }
    }
    private LocalDate getCurrentAcademicYearEnd() {
        LocalDate start = getCurrentAcademicYearStart();
        return start.plusYears(1).minusDays(1);
    }

    @Override
    public Map<String, List<Chambre>> getChambresNonReservePourToutesUniversites() {

        // 1. Get current academic year dates
        LocalDate currentYearStart = getCurrentAcademicYearStart();
        LocalDate currentYearEnd = getCurrentAcademicYearEnd();
        // 2. Get all universities
        List<Universite> allUniversites = universiteRepository.findAll();
        // 3. For each university, get non-reserved chambres
        Map<String, List<Chambre>> result = new HashMap<>();

        for (Universite universite : allUniversites) {
            // Get all chambres for this university
            List<Chambre> allChambres = chambreRepository.findByBlocFoyerUniversiteNomUniversite(universite.getNomUniversite());
            // Filter out reserved chambres
            List<Chambre> availableChambres = allChambres.stream()
                    .filter(chambre -> !hasValidReservationInCurrentYear(chambre, currentYearStart, currentYearEnd))
                    .collect(Collectors.toList());
            result.put(universite.getNomUniversite(), availableChambres);
        }

        return result;
    }

}
