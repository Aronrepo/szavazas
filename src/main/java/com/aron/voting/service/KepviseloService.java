package com.aron.voting.service;

import com.aron.voting.dao.model.Kepviselo;
import com.aron.voting.repositories.KepviseloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KepviseloService {
    private KepviseloRepository kepviseloRepository;

    @Autowired
    public KepviseloService(KepviseloRepository kepviseloRepository) {
        this.kepviseloRepository = kepviseloRepository;
    }

    public Kepviselo getKepviselo(String kepviseloAzonosito) {
        return kepviseloRepository.findKepviseloByKepviseloKod(kepviseloAzonosito);
    }
}
