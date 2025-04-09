package kroryi.apikeymanager.controller;

import kroryi.apikeymanager.dto.CalendarEvent;
import kroryi.apikeymanager.entity.Reservation;
import kroryi.apikeymanager.repository.ReservationRepository;
import kroryi.apikeymanager.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    private final ReservationRepository reservationRepository;
    @GetMapping("/all")
    public List<CalendarEvent> getAllEvents() {
        return reservationRepository.findAll().stream()
                .map(r -> new CalendarEvent(
                        r.getId(),
                        r.getName() + ": " + r.getMemo(),
                        r.getReservationDate().toString()
                ))
                .toList();
    }

    @PostMapping
    public Reservation create(@RequestBody Reservation reservation) {
        return reservationService.save(reservation);
    }

    @GetMapping("/{date}")
    public List<Reservation> getByDate(@PathVariable String date) {
        return reservationService.getByDate(LocalDate.parse(date));
    }
}