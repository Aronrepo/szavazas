package com.aron.voting.service;

import com.aron.voting.dao.model.*;
import com.aron.voting.dto.KepviseloSzavazatDTO;
import com.aron.voting.dto.SzavazasEredmenyDTO;
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

    private final KepviseloService kepviseloService;
    private final SzavazasRepository szavazasRepository;

    private final SzavazatokRepository szavazatokRepository;

    private static final Eredmeny ELFOGADOTT_EREDMENY = Eredmeny.F;
    private static final Eredmeny ELUTASITOTT_EREDMENY = Eredmeny.U;
    private static final int OSSZES_KEPVISELO_FELE = 100;

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

        return new KepviseloSzavazatDTO(szavazat);
    }

    public SzavazasEredmenyDTO szavazasEredmenyLekerdezese(String szavazasAzonosito) {

        Szavazas szavazas = szavazasRepository.findBySzavazasId(szavazasAzonosito);
        if(szavazas == null) {
            throw new SzavazasNotFoundException("Nincs ilyen szavazas: " + szavazasAzonosito);
        }

        int kepviselokSzama = szavazas.getSzavazatok().size();
        int igenekSzama = (int) szavazas.getSzavazatok().stream().filter(szavazatok -> szavazatok.getSzavazat().name().equals("i")).count();
        int nemekSzama = (int) szavazas.getSzavazatok().stream().filter(szavazatok -> szavazatok.getSzavazat().name().equals("n")).count();
        int tartozkodokSzama = (int) szavazas.getSzavazatok().stream().filter(szavazatok -> szavazatok.getSzavazat().name().equals("t")).count();

        Eredmeny eredmeny;

        switch (szavazas.getTipus().name()) {
            case "j":
                eredmeny = ELFOGADOTT_EREDMENY;
                break;
            case "e":
                eredmeny = (igenekSzama > kepviselokSzama / 2) ? ELFOGADOTT_EREDMENY : ELUTASITOTT_EREDMENY;
                break;
            case "m":
                eredmeny = (igenekSzama > OSSZES_KEPVISELO_FELE) ? ELFOGADOTT_EREDMENY : ELUTASITOTT_EREDMENY;
                break;
            default:
                throw new IllegalArgumentException("Ismeretlen szavazas tipus: " + szavazas.getTipus().name());
        }

        return szavazasEredmenyDTOkeszito(eredmeny, kepviselokSzama, igenekSzama, nemekSzama, tartozkodokSzama);
    }

    private SzavazasEredmenyDTO szavazasEredmenyDTOkeszito(Eredmeny eredmeny, int kepviselokSzama, int igenekSzama, int nemekSzama, int tartozkodokSzama) {
        return new SzavazasEredmenyDTO(eredmeny.toString(), kepviselokSzama, igenekSzama, nemekSzama, tartozkodokSzama);
    }
}
