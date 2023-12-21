package com.aron.voting.controller;

import com.aron.voting.dto.SzavazasValaszDTO;
import com.aron.voting.dto.UjSzavazasDTO;
import com.aron.voting.service.SzavazasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/szavazasok")
public class SzavazasController {

    private final SzavazasService szavazasService;

    @Autowired
    public SzavazasController(SzavazasService szavazasService) {
        this.szavazasService = szavazasService;
    }

    @PostMapping("/szavazas")
    public ResponseEntity<SzavazasValaszDTO> hozzaadUjSzavazast(@RequestBody UjSzavazasDTO ujSzavazas) {
        SzavazasValaszDTO szavazasValaszDTO = new SzavazasValaszDTO(szavazasService.ujSzavazas(ujSzavazas));
        return new ResponseEntity<>(szavazasValaszDTO, HttpStatus.CREATED);
    }
}
