package com.aron.voting.dto;

import com.aron.voting.dao.model.Tipus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record UjSzavazasDTO(
        @NotNull LocalDateTime idopont,
        @NotBlank String targy,
        @NotNull Tipus tipus,
        @NotBlank String elnok,
        @Valid Set<@NotNull SzavazatDTO> szavazatok
) {
}
