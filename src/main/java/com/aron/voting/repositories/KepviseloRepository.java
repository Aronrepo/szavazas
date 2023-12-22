package com.aron.voting.repositories;

import com.aron.voting.dao.model.Kepviselo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KepviseloRepository extends JpaRepository<Kepviselo, Long> {

    Kepviselo findKepviseloByKepviseloKod(String kepviselo_kod);

}
