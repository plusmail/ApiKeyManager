package kroryi.apikeymanager.repository;

import kroryi.apikeymanager.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByReservationDate(LocalDate date);
}