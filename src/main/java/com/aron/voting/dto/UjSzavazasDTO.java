package com.aron.voting.dto;

import com.aron.voting.dao.model.Tipus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record UjSzavazasDTO(
        @NotNull LocalDateTime idopont,
        @NotNull String targy,
        @NotNull Tipus tipus,
        @NotNull String elnok,
        @NotNull Set<SzavazatDTO> szavazatok
) {
}
