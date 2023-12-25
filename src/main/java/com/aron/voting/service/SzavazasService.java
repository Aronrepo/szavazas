package com.aron.voting.service;

import com.aron.voting.dao.model.*;
import com.aron.voting.dto.*;
import com.aron.voting.exception.*;
import com.aron.voting.repositories.SzavazasRepository;
import com.aron.voting.repositories.SzavazatokRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SzavazasService {

    private final KepviseloService kepviseloService;
    private final SzavazasRepository szavazasRepository;

    private final SzavazatokRepository szavazatokRepository;

    private static final Eredmeny ELFOGADOTT_EREDMENY = Eredmeny.F;
    private static final Eredmeny ELUTASITOTT_EREDMENY = Eredmeny.U;
    private static final int OSSZES_KEPVISELO_FELE = 100;

    private static final Tipus EGYSZERU_TIPUS = Tipus.e;
    private static final Tipus MINOSITETT_TIPUS = Tipus.m;

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

        boolean elnoknekVanSzavazata = szavazas.getSzavazatok().stream()
                .anyMatch(szavazatok1 -> szavazatok1.getKepviselo().equals(elnok));

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

        String szavazat = szavazas.getSzavazatok().stream()
                .filter(szavazatok -> szavazatok.getKepviselo()
                        .getKepviseloKod().equals(kepviseloAzonosito))
                .findFirst().orElseThrow(() -> new KepviseloNotFoundException("Nincs ilyen kepviselo az adott szavazáson: " + kepviseloAzonosito)).getSzavazat().toString();

        return new KepviseloSzavazatDTO(szavazat);
    }

    public SzavazasEredmenyDTO szavazasEredmenyLekerdezese(String szavazasAzonosito) {

        Szavazas szavazas = szavazasRepository.findBySzavazasId(szavazasAzonosito);
        if(szavazas == null) {
            throw new SzavazasNotFoundException("Nincs ilyen szavazas: " + szavazasAzonosito);
        }

        Eredmeny eredmeny = eredmenyKiszamolo(szavazas);
        int kepviselokSzama = kepviselokSzamaKiszamolo(szavazas);
        int igenekSzama = igenekSzamaKiszamolo(szavazas);
        int nemekSzama = nemekSzamaKiszamolo(szavazas);
        int tartozkodokSzama = tartozkodokSzamaKiszamolo(szavazas);

        return szavazasEredmenyDTOkeszito(eredmeny, kepviselokSzama, igenekSzama, nemekSzama, tartozkodokSzama);
    }

    private SzavazasEredmenyDTO szavazasEredmenyDTOkeszito(Eredmeny eredmeny, int kepviselokSzama, int igenekSzama, int nemekSzama, int tartozkodokSzama) {
        return new SzavazasEredmenyDTO(eredmeny.toString(), kepviselokSzama, igenekSzama, nemekSzama, tartozkodokSzama);
    }

    private int kepviselokSzamaKiszamolo(Szavazas szavazas) {
        return szavazas.getSzavazatok().size();
    }

    private int igenekSzamaKiszamolo(Szavazas szavazas) {
        return (int) szavazas.getSzavazatok().stream().filter(szavazatok -> szavazatok.getSzavazat().name().equals("i")).count();
    }

    private int nemekSzamaKiszamolo(Szavazas szavazas) {
        return (int) szavazas.getSzavazatok().stream().filter(szavazatok -> szavazatok.getSzavazat().name().equals("n")).count();
    }

    private int tartozkodokSzamaKiszamolo(Szavazas szavazas) {
        return (int) szavazas.getSzavazatok().stream().filter(szavazatok -> szavazatok.getSzavazat().name().equals("t")).count();
    }

    public Map<String, List<NapiSzavazasDTO>> napiSzavazasLekerdezese(LocalDate date) {
        List<Szavazas> szavazasok = szavazasRepository.findByIdopontBetween(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
        List<NapiSzavazasDTO> napiSzavazasDTOS = szavazasok.stream().map(szavazas -> szavazasKonvertalasaSzavazasDTOra(szavazas))
                .toList();

        Map<String, List<NapiSzavazasDTO>> result = new HashMap<>();
        result.put("szavazasok", napiSzavazasDTOS);
        return result;
    }

    private NapiSzavazasDTO szavazasKonvertalasaSzavazasDTOra(Szavazas szavazas) {
        return new NapiSzavazasDTO(szavazas.getIdopont(),
                szavazas.getTargy(),
                szavazas.getTipus().toString(),
                szavazas.getElnok().getKepviseloKod(),
                eredmenyKiszamolo(szavazas).toString(),
                szavazas.getSzavazatok().size(),
                szavazatokKonvertalasaEredmenySzavazatDTOra(szavazas.getSzavazatok()));
    }

    private Set<EredmenySzavazatDTO> szavazatokKonvertalasaEredmenySzavazatDTOra(Set<Szavazatok> szavazatok) {
        return szavazatok.stream()
                .map(szavazatok1 -> new EredmenySzavazatDTO(szavazatok1.getKepviselo().getKepviseloKod(), szavazatok1.getSzavazat().toString()))
                .collect(Collectors.toSet());
    }

    private Eredmeny eredmenyKiszamolo(Szavazas szavazas) {
        int igenekSzama = igenekSzamaKiszamolo(szavazas);
        int kepviselokSzama = kepviselokSzamaKiszamolo(szavazas);

        switch (szavazas.getTipus().name()) {
            case "j":
                return ELFOGADOTT_EREDMENY;
            case "e":
                return (igenekSzama > kepviselokSzama / 2) ? ELFOGADOTT_EREDMENY : ELUTASITOTT_EREDMENY;
            case "m":
                return (igenekSzama > OSSZES_KEPVISELO_FELE) ? ELFOGADOTT_EREDMENY : ELUTASITOTT_EREDMENY;
            default:
                throw new IllegalArgumentException("Ismeretlen szavazas tipus: " + szavazas.getTipus().name());
        }
    }

    public KepviseloAtlagDTO kepviseloAtlagLekerdezese(LocalDate idoszakKezdete, LocalDate idoszakVege) {
        Optional optionalDouble = szavazasRepository.findAverageAttendanceByTimePeriod(EGYSZERU_TIPUS, MINOSITETT_TIPUS, LocalDateTime.of(idoszakKezdete, LocalTime.MIN), LocalDateTime.of(idoszakVege, LocalTime.MAX));
        if(optionalDouble.isEmpty()) {
            throw new IdoszakUresException("Az időkban nem volt szavazás");
        } else {
            BigDecimal szavazasokDouble = new BigDecimal(optionalDouble.get().toString()).setScale(2, RoundingMode.HALF_UP);
            return new KepviseloAtlagDTO(szavazasokDouble);
        }
    }
}
