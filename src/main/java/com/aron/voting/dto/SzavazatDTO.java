package com.aron.voting.dto;

import com.aron.voting.dao.model.Szavazat;

public record SzavazatDTO(
        String kepviselo,
        Szavazat szavazat
) {
}
