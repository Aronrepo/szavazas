package com.aron.voting.service;

import com.aron.voting.dao.model.Kepviselo;
import com.aron.voting.dao.model.Szavazas;
import com.aron.voting.dao.model.Szavazat;
import com.aron.voting.dao.model.Szavazatok;
import com.aron.voting.dto.KepviseloSzavazatDTO;
import com.aron.voting.dto.SzavazasValaszDTO;
import com.aron.voting.dto.UjSzavazasDTO;
import com.aron.voting.exception.*;
import com.aron.voting.repositories.SzavazasRepository;
import com.aron.voting.repositories.SzavazatokRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SzavazasService {

    private KepviseloService kepviseloService;
    private SzavazasRepository szavazasRepository;

    private SzavazatokRepository szavazatokRepository;

    @Autowired
    public SzavazasService(KepviseloService kepviseloService, SzavazasRepository szavazasRepository, SzavazatokRepository szavazatokRepository) {

        this.kepviseloService = kepviseloService;
        this.szavazasRepository = szavazasRepository;
        this.szavazatokRepository = szavazatokRepository;
    }

    @Transactional
    public String ujSzavazas(UjSzavazasDTO ujSzavazas) {
        Kepviselo elnok = kepviseloService.getKepviselo(ujSzavazas.elnok());

        Szavazas szavazas = new Szavazas(
                ujSzavazas.idopont(),
                ujSzavazas.targy(),
                ujSzavazas.tipus(),
                elnok
        );

        Set<Szavazatok> szavazatok = ujSzavazas.szavazatok().stream()
                .map(dto -> {
                    Kepviselo kepviselo = kepviseloService.getKepviselo(dto.kepviselo());
                    Szavazat szavazat = dto.szavazat();
                    return new Szavazatok(kepviselo, szavazat);

                })
                .collect(Collectors.toSet());

        szavazas.setSzavazatok(szavazatok);

        boolean elnoknekVanSzavazata = szavazas.getSzavazatok().stream().anyMatch(szavazatok1 -> szavazatok1.getKepviselo().equals(elnok));

        if(!elnoknekVanSzavazata) {
            throw new ElnoknekNincsSzavazataException("Az elnöknek nincs szavazata");
        }

        boolean egyszerreCsakEgySzavazatLehet = kepviseloknekNincsKettoSzavazata(szavazatok);

        if(!egyszerreCsakEgySzavazatLehet) {
            throw new TobbMintEgySzavazatException("Egy képviselő csak egyszer szavazhat");
        }


        Szavazas szavazasEbbenAzIdoponban = szavazasRepository.findSzavazasByIdopont(szavazas.getIdopont());

        if(szavazasEbbenAzIdoponban != null) {
            throw new SzavazasAzonosIdobenException("Ugyanarra az időpontra nem lehet két szavazást felvinni");
        }

        Szavazas savedSzavazas = szavazasRepository.save(szavazas);

        return savedSzavazas.getSzavazasId();
    }

    private boolean kepviseloknekNincsKettoSzavazata(Set<Szavazatok> osszesSzavazat) {
        Set<Kepviselo> talaltKepviselok = new HashSet<>();

        for (Szavazatok szavazatok : osszesSzavazat) {
            if(!talaltKepviselok.add(szavazatok.getKepviselo())) {
                return false;
            }

        }
        return true;

    }

    public KepviseloSzavazatDTO kepviseloSzavazatLekerdezese(String szavazasAzonosito, String kepviseloAzonosito) {
        Kepviselo kepviselo = kepviseloService.getKepviselo(kepviseloAzonosito);
        if(kepviselo == null) {
            throw new KepviseloNotFoundException("Nincs ilyen kepviselo: " + kepviseloAzonosito);
        }
        Szavazas szavazas = szavazasRepository.findBySzavazasId(szavazasAzonosito);
        if(szavazas == null) {
            throw new SzavazasNotFoundException("Nincs ilyen szavazas: " + szavazasAzonosito);
        }

        String szavazat = szavazas.getSzavazatok().stream().filter(szavazatok -> szavazatok.getKepviselo().getKepviseloKod().equals(kepviseloAzonosito)).findFirst().orElse(null).getSzavazat().toString();

        //Szavazatok szavazatok = szavazatokRepository.findSzavazatokBySzavazas_SzavazasIdAndKepviselo(szavazasAzonosito, kepviselo);
        //return new KepviseloSzavazatDTO(szavazatok.getSzavazat().toString());
        return new KepviseloSzavazatDTO(szavazat);
    }
}
