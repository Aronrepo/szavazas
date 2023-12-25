package com.aron.voting.dao.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Szavazatok {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Szavazatok(Kepviselo kepviselo, Szavazat szavazat) {
        this.kepviselo = kepviselo;
        this.szavazat = szavazat;
    }

    @ManyToMany(mappedBy = "szavazatok", cascade = CascadeType.MERGE)
    private Set<Szavazas> szavazasSet = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "kepviselo_id")
    private Kepviselo kepviselo;

    @Enumerated(EnumType.STRING)
    private Szavazat szavazat;
}
