package com.esprit.gestionfoyer.services;

import com.esprit.gestionfoyer.entites.Chambre;
import com.esprit.gestionfoyer.entites.Etudiant;
import com.esprit.gestionfoyer.entites.Reservation;
import com.esprit.gestionfoyer.entites.TypeChambre;
import com.esprit.gestionfoyer.repositories.ChambreRepository;
import com.esprit.gestionfoyer.repositories.EtudiantRepository;
import com.esprit.gestionfoyer.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationServiceImpl implements IReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;


    @Override
    public List<Reservation> retrieveAllReservation() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation updateReservation(Reservation res) {
        // Check if reservation exists before updating
        if (reservationRepository.existsById(res.getIdReservation())) {
            return reservationRepository.save(res);
        }
        throw new RuntimeException("Reservation not found with id: " + res.getIdReservation());
    }

    @Override
    public Reservation retrieveReservation(String idReservation) {
        return reservationRepository.findById(idReservation)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + idReservation));
    }
    @Override
    public Reservation ajouterReservation(long idChambre, long cinEtudiant) {
        // 1. Find the chambre and check capacity
        Chambre chambre = chambreRepository.findById(idChambre).orElseThrow(() -> new RuntimeException("Chambre not found with id: " + idChambre));
        // 2. Find the student+-+-
        Etudiant etudiant = etudiantRepository.findByCin(cinEtudiant).orElseThrow(() -> new RuntimeException("Etudiant not found with CIN: " + cinEtudiant));
        // 3. Check if chambre capacity is not exceeded
        if (!isChambreCapacityAvailable(chambre)) {
            throw new RuntimeException("Chambre capacity exceeded for type: " + chambre.getTypeC());
        }
        // 4. Create reservation number format: numChambre-nomBloc-anneeUniversitaire
        String numReservation = generateReservationNumber(chambre);
        // 5. Create and save reservation
        Reservation reservation = new Reservation();
        reservation.setIdReservation(numReservation);
        reservation.setAnneeUniversitaire(LocalDate.now());
        reservation.setEstValide(true);
        reservation.setChambre(chambre);
        // 6. Add student to reservation .
        reservation.getEtudiants().add(etudiant);

        return reservationRepository.save(reservation);
    }
    //comentaire
    private boolean isChambreCapacityAvailable(Chambre chambre) {
        // Count current valid reservations for this chambre
        List<Reservation> reservations = reservationRepository.findByChambreAndEstValideTrue(chambre);
        // Check against chambre type capacity
        return reservations.size() < getMaxCapacityByType(chambre.getTypeC());
    }

    private int getMaxCapacityByType(TypeChambre type) {
        switch (type) {
            case SIMPLE: return 1;
            case DOUBLE: return 2;
            case TRIPLE: return 3;
            default: return 0;
        }
    }

    private String generateReservationNumber(Chambre chambre) {
        String numChambre = String.valueOf(chambre.getNumeroChambre());
        String nomBloc = chambre.getBloc().getNomBloc();
        String anneeUniversitaire = String.valueOf(LocalDate.now().getYear());

        return numChambre + "-" + nomBloc + "-" + anneeUniversitaire;
    }

    @Override
    public Reservation annulerReservation(long cinEtudiant) {
        // 1. Find the student by CIN
        Etudiant etudiant = etudiantRepository.findByCin(cinEtudiant)
                .orElseThrow(() -> new RuntimeException("Etudiant not found with CIN: " + cinEtudiant));

        // 2. Find the student's valid reservation
        Reservation reservation = findValidReservationByEtudiant(etudiant);
        if (reservation == null) {
            throw new RuntimeException("No valid reservation found for student with CIN: " + cinEtudiant);
        }

        // 3. Get the chambre associated with the reservation
        Chambre chambre = reservation.getChambre();

        // 4. Remove student from reservation (ManyToMany)
        reservation.getEtudiants().remove(etudiant);

        // 5. Check if reservation becomes empty after removal
        if (reservation.getEtudiants().isEmpty()) {
            // If no students left, invalidate the entire reservation
            reservation.setEstValide(false);

            // Remove chambre association (set to null)
            reservation.setChambre(null);

            // If chambre exists, we've effectively freed up capacity
            if (chambre != null) {
                // Capacity is automatically updated since we're removing a reservation
                // The chambre's capacity is determined by its type, not stored as a field
                System.out.println("Chambre " + chambre.getNumeroChambre() + " capacity updated - reservation removed");
            }
        }
        // 6. Save the updated reservation
        return reservationRepository.save(reservation);
    }

    private Reservation findValidReservationByEtudiant(Etudiant etudiant) {
        // Get all reservations for this student and find a valid one
        List<Reservation> reservations = reservationRepository.findByEtudiantsAndEstValideTrue(etudiant);
        if (reservations.isEmpty()) {
            return null;
        }
        // Return the first valid reservation (you might want more complex logic here)
        return reservations.get(0);
    }


    @Override
    public List<Reservation> getReservationParAnneeUniversitaireEtNomUniversite(
            LocalDate anneeUniversite, String nomUniversite) {

        return reservationRepository.findByChambreBlocFoyerUniversiteNomUniversiteAndAnneeUniversitaire(
                nomUniversite, anneeUniversite);
    }
}
