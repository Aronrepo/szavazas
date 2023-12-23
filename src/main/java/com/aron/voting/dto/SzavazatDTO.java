package com.aron.voting.dto;

import com.aron.voting.dao.model.Szavazat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SzavazatDTO(
        @NotBlank String kepviselo,
        @NotNull Szavazat szavazat
) {
}
