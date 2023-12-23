package com.aron.voting.repositories;

import com.aron.voting.dao.model.Kepviselo;
import com.aron.voting.dao.model.Szavazas;
import com.aron.voting.dao.model.Szavazatok;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SzavazatokRepository extends JpaRepository<Szavazatok, Long> {


}
