package com.aron.voting.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record NapiSzavazasDTO(LocalDateTime idopont, String targy, String tipus, String elnok, String eredmeny, int kepviselokSzama, Set<EredmenySzavazatDTO> eredmenySzavazatDTOS) {
}
