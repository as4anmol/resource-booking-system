package com.anmol.bookingsystem.service;

import com.anmol.bookingsystem.dto.ReservationRequestDTO;
import com.anmol.bookingsystem.dto.ReservationResponseDTO;
import com.anmol.bookingsystem.entity.*;
import com.anmol.bookingsystem.repository.ReservationRepository;
import com.anmol.bookingsystem.repository.ResourceRepository;
import com.anmol.bookingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationResponseDTO createReservation(ReservationRequestDTO dto, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + dto.getResourceId()));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setPrice(dto.getPrice());
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation saved = reservationRepository.save(reservation);
        return toDTO(saved);
    }

    public Page<ReservationResponseDTO> getReservations(
            Authentication authentication,
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        Specification<Reservation> spec = (root, query, cb) -> cb.conjunction();

        if (!isAdmin) {
            Long userId = currentUser.getId();
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        Page<Reservation> reservations = reservationRepository.findAll(spec, pageable);
        return reservations.map(this::toDTO);
    }

    public ReservationResponseDTO getReservationById(Long id, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = reservation.getUser().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("You are not authorized to view this reservation");
        }

        return toDTO(reservation);
    }

    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Reservation not found with id: " + id);
        }
        reservationRepository.deleteById(id);
    }

    public ReservationResponseDTO updateReservationStatus(Long id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        reservation.setStatus(status);
        Reservation updated = reservationRepository.save(reservation);
        return toDTO(updated);
    }

    private ReservationResponseDTO toDTO(Reservation r) {
        return new ReservationResponseDTO(
                r.getId(),
                r.getUser().getId(),
                r.getUser().getUsername(),
                r.getResource().getId(),
                r.getResource().getName(),
                r.getStartTime(),
                r.getEndTime(),
                r.getPrice(),
                r.getStatus());
    }
}