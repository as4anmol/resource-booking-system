package com.anmol.bookingsystem;

import com.anmol.bookingsystem.dto.ReservationRequestDTO;
import com.anmol.bookingsystem.entity.Reservation;
import com.anmol.bookingsystem.entity.ReservationStatus;
import com.anmol.bookingsystem.entity.Resource;
import com.anmol.bookingsystem.entity.Role;
import com.anmol.bookingsystem.entity.User;
import com.anmol.bookingsystem.exception.UnauthorizedAccessException;
import com.anmol.bookingsystem.repository.ReservationRepository;
import com.anmol.bookingsystem.repository.ResourceRepository;
import com.anmol.bookingsystem.repository.UserRepository;
import com.anmol.bookingsystem.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationSecurityTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReservationService reservationService;

    private User owner;
    private User otherUser;
    private Resource resource;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        owner = user(1L, "owner", Role.USER);
        otherUser = user(2L, "other", Role.USER);
        resource = new Resource(10L, "Meeting Room", "ROOM", "Main room", true);
        reservation = new Reservation(20L, owner, resource,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2),
                BigDecimal.valueOf(100), ReservationStatus.PENDING);
    }

    @Test
    void userCannotReadAnotherUsersReservation() {
        when(authentication.getName()).thenReturn("other");
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(otherUser));
        when(reservationRepository.findById(20L)).thenReturn(Optional.of(reservation));

        assertThrows(UnauthorizedAccessException.class,
                () -> reservationService.getReservationById(20L, authentication));
    }

    @Test
    void ownerCanReadOwnReservation() {
        when(authentication.getName()).thenReturn("owner");
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(reservationRepository.findById(20L)).thenReturn(Optional.of(reservation));

        assertEquals(20L, reservationService.getReservationById(20L, authentication).getId());
    }

    @Test
    void overlappingReservationIsRejected() {
        ReservationRequestDTO request = request(10L, BigDecimal.valueOf(125));
        when(authentication.getName()).thenReturn("owner");
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource));
        when(reservationRepository.existsOverlappingReservation(eq(10L), any(), any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(request, authentication));
    }

    @Test
    void invalidPriceRangeIsRejectedBeforeQuery() {
        assertThrows(IllegalArgumentException.class,
                () -> reservationService.getReservations(authentication, null,
                        BigDecimal.valueOf(200), BigDecimal.valueOf(100), PageRequest.of(0, 10)));
    }

    @Test
    void unsupportedSortFieldIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> reservationService.getReservations(authentication, null, null, null,
                        PageRequest.of(0, 10, Sort.by("password"))));
    }

    @Test
    void defaultSortIsAppliedToReservationSearch() {
        when(authentication.getName()).thenReturn("owner");
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(reservationRepository.findAll(
            org.mockito.ArgumentMatchers.<org.springframework.data.jpa.domain.Specification<Reservation>>any(),
            any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        reservationService.getReservations(authentication, null, null, null, PageRequest.of(0, 10));

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(reservationRepository).findAll(
            org.mockito.ArgumentMatchers.<org.springframework.data.jpa.domain.Specification<Reservation>>any(),
            pageable.capture());
        assertEquals("startTime", pageable.getValue().getSort().getOrderFor("startTime").getProperty());
    }

    @Test
    void adminCanUpdateReservationStatus() {
        User admin = user(3L, "admin", Role.ADMIN);
        when(reservationRepository.findById(20L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        reservationService.updateReservationStatus(20L, ReservationStatus.CONFIRMED);

        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
    }

    private User user(Long id, String username, Role role) {
        return new User(id, username, "hashed", role);
    }

    private ReservationRequestDTO request(Long resourceId, BigDecimal price) {
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setResourceId(resourceId);
        request.setStartTime(LocalDateTime.now().plusDays(2));
        request.setEndTime(LocalDateTime.now().plusDays(2).plusHours(1));
        request.setPrice(price);
        return request;
    }
}