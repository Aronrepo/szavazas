package com.aron.voting.repositories;

import com.aron.voting.dao.model.Szavazas;
import com.aron.voting.dao.model.Tipus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SzavazasRepository extends JpaRepository<Szavazas, Long> {
    Szavazas findSzavazasByIdopont(LocalDateTime localDateTime);

    Szavazas findBySzavazasId(String azonosito);

    List<Szavazas> findByIdopontBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("SELECT AVG(subquery.voteCount) as szavat_per_epviselo FROM (" +
            "SELECT COUNT(szav.kepviselo) AS voteCount " +
            "FROM Szavazas sz " +
            "JOIN sz.szavazatok szav " +
            "WHERE " +
            "sz.tipus IN (:egyszeruTipus, :minositettTipus) " +
            "AND sz.idopont BETWEEN :startDate AND :endDate " +
            "GROUP BY szav.kepviselo" +
            ") AS subquery")
    Optional<Double> findAverageAttendanceByTimePeriod(
            Tipus egyszeruTipus,
            Tipus minositettTipus,
            LocalDateTime startDate,
            LocalDateTime endDate
    );


}
