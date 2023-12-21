package com.aron.voting.repositories;

import com.aron.voting.dao.model.Kepviselo;
import com.aron.voting.dao.model.Szavazas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SzavazasRepository extends JpaRepository<Szavazas, Long> {

}
