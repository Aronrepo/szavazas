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
public class Kepviselo {



    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kepviselo_sequence")
    @SequenceGenerator(name = "kepviselo_sequence", sequenceName = "kepviselo_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "kepviseloKod",  unique = true)
    private String kepviseloKod;

    @PrePersist
    public void generateVotingId() {
        this.kepviseloKod = "Kepviselo" + this.id;
    }

    @OneToMany(mappedBy = "elnok", cascade = CascadeType.MERGE)
    private Set<Szavazas> szavazasSet = new HashSet<>();


    @OneToMany(mappedBy = "kepviselo")
    private Set<Szavazatok> szavazatok = new HashSet<>();




}
