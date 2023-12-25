package com.aron.voting.service;

import com.aron.voting.dao.model.Kepviselo;
import com.aron.voting.dao.model.Szavazas;
import com.aron.voting.dao.model.Szavazat;
import com.aron.voting.dao.model.Tipus;
import com.aron.voting.dto.SzavazatDTO;
import com.aron.voting.dto.UjSzavazasDTO;
import com.aron.voting.exception.ElnoknekNincsSzavazataException;
import com.aron.voting.repositories.SzavazasRepository;
import com.aron.voting.repositories.SzavazatokRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SzavazasServiceTest {

    @Mock
    private KepviseloService kepviseloService;

    @Mock
    private SzavazasRepository szavazasRepository;

    @Mock
    private SzavazatokRepository szavazatokRepository;

    @InjectMocks
    private SzavazasService szavazasService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUjSzavazasSuccess() {
        SzavazatDTO szavazatDTO = new SzavazatDTO("Kepviselo1", Szavazat.i);
        Set<SzavazatDTO> szavazatDTOset = new HashSet<>();
        szavazatDTOset.add(szavazatDTO);
        UjSzavazasDTO ujSzavazasDTO = new UjSzavazasDTO(LocalDateTime.now(), "Szavazás tárgya", Tipus.j, "Kepviselo1", szavazatDTOset);

        Kepviselo kepviselo = new Kepviselo();
        Szavazas szavazas = new Szavazas();

        when(kepviseloService.getKepviselo(any())).thenReturn(kepviselo);
        when(szavazasRepository.save(any())).thenReturn(szavazas);

        String result = szavazasService.ujSzavazas(ujSzavazasDTO);

        assertDoesNotThrow(() -> szavazasService.ujSzavazas(ujSzavazasDTO));

    }

    @Test
    public void testUjSzavazasElnoknekNincsSzavazataException() {
        //SzavazatDTO szavazatDTO = new SzavazatDTO("Kepviselo1", Szavazat.i);
        Set<SzavazatDTO> szavazatDTOset = new HashSet<>();
        //szavazatDTOset.add(szavazatDTO);
        UjSzavazasDTO ujSzavazasDTO = new UjSzavazasDTO(LocalDateTime.now(), "Szavazás tárgya", Tipus.j, "Kepviselo1", szavazatDTOset);

        Kepviselo kepviselo = new Kepviselo();
        Szavazas szavazas = new Szavazas();

        when(kepviseloService.getKepviselo(any())).thenReturn(kepviselo);
        when(szavazasRepository.save(any())).thenReturn(szavazas);

        ElnoknekNincsSzavazataException thrown = assertThrows(
                ElnoknekNincsSzavazataException.class,
                () -> szavazasService.ujSzavazas(ujSzavazasDTO),
                "Az elvárás az, hogy az ujSzavazas() kivételt dobjon, de nem dobot "
        );

        assertTrue(thrown.getMessage().contains("Az elnöknek nincs szavazata"));

    }
}