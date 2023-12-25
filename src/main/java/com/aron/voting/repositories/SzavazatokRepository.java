package com.aron.voting.repositories;

import com.aron.voting.dao.model.Szavazatok;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SzavazatokRepository extends JpaRepository<Szavazatok, Long> {


}
