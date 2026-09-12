package com.anmol.bookingsystem.repository;

import com.anmol.bookingsystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.resource.id = :resourceId " +
           "AND r.status <> 'CANCELLED' " +
           "AND r.startTime < :endTime AND r.endTime > :startTime")
    boolean existsOverlappingReservation(@Param("resourceId") Long resourceId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);
}