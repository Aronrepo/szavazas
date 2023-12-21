package com.aron.voting.repositories;

import com.aron.voting.dao.model.Szavazas;
import com.aron.voting.dao.model.Szavazatok;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SzavazatokRepository extends JpaRepository<Szavazatok, Long> {
}
