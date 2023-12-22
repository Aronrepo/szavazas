package com.aron.voting.dto;

import com.aron.voting.dao.model.Tipus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record UjSzavazasDTO(
        LocalDateTime idopont,
        String targy,
        Tipus tipus,
        String elnok,
        Set<SzavazatDTO> szavazatok
) {
}
