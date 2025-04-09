package kroryi.apikeymanager.service;

import kroryi.apikeymanager.entity.Reservation;
import kroryi.apikeymanager.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation save(Reservation r) {
        return reservationRepository.save(r);
    }

    public List<Reservation> getByDate(LocalDate date) {
        return reservationRepository.findByReservationDate(date);
    }
    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

}