package com.aron.voting.repositories;

import com.aron.voting.dao.model.Kepviselo;
import com.aron.voting.dao.model.Szavazas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SzavazasRepository extends JpaRepository<Szavazas, Long> {
    Szavazas findSzavazasByIdopont(LocalDateTime localDateTime);

    Szavazas findBySzavazasId(String azonosito);

    List<Szavazas> findByIdopontBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);


}
