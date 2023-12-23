package com.aron.voting.controller;

import com.aron.voting.dto.KepviseloSzavazatDTO;
import com.aron.voting.dto.SzavazasValaszDTO;
import com.aron.voting.dto.UjSzavazasDTO;
import com.aron.voting.service.SzavazasService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/szavazasok")
public class SzavazasController {

    private final SzavazasService szavazasService;

    @Autowired
    public SzavazasController(SzavazasService szavazasService) {
        this.szavazasService = szavazasService;
    }

    @PostMapping("/szavazas")
    public ResponseEntity<?> hozzaadUjSzavazast(@Valid @RequestBody UjSzavazasDTO ujSzavazas) {

        String szavazasId = szavazasService.ujSzavazas(ujSzavazas);
        SzavazasValaszDTO szavazasValaszDTO = new SzavazasValaszDTO(szavazasId);
        return new ResponseEntity<>(szavazasValaszDTO, HttpStatus.CREATED);
    }

    @GetMapping("/szavazat")
    public ResponseEntity<KepviseloSzavazatDTO> kepviseloSzavazatLekerdezese(@RequestParam String szavazas, String kepviselo
    ) {
        return new ResponseEntity<>(szavazasService.kepviseloSzavazatLekerdezese(szavazas, kepviselo), HttpStatus.OK);
    }
}
