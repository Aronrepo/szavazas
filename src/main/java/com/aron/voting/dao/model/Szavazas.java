package com.aron.voting.dao.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Szavazas {



    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "szavazas_sequence")
    @SequenceGenerator(name = "szavazas_sequence", sequenceName = "szavazas_sequence", allocationSize = 1)
    private Long id;


    @Column(name = "szavazasId", unique = true)
    private String szavazasId;

    @PrePersist
    public void generateVotingId() {
        this.szavazasId = "OJ" + this.id;
    }

    private LocalDateTime idopont;
    private String targy;
    @Enumerated(EnumType.STRING)
    private Tipus tipus;

    public Szavazas(LocalDateTime idopont, String targy, Tipus tipus, Kepviselo elnok) {
        this.idopont = idopont;
        this.targy = targy;
        this.tipus = tipus;
        this.elnok = elnok;

    }

    @ManyToOne
    @JoinColumn(name = "elnok_id")
    private Kepviselo elnok;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "szavazas_szavazatok_map",
        joinColumns = @JoinColumn(name = "szavazas_id"),
        inverseJoinColumns = @JoinColumn(name = "szavazatok_id")
    )
    Set<Szavazatok> szavazatok;



}
