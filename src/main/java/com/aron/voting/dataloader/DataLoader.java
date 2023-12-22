package com.aron.voting.dataloader;

import com.aron.voting.dao.model.Kepviselo;
import com.aron.voting.repositories.KepviseloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;
@Component
public class DataLoader implements CommandLineRunner {
    private final KepviseloRepository kepviseloRepository;

    @Autowired
    public DataLoader(KepviseloRepository kepviseloRepository) {
        this.kepviseloRepository = kepviseloRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Kepviselo kepviselo1 = Kepviselo.builder().build();
        Kepviselo kepviselo2 = Kepviselo.builder().build();
        Kepviselo kepviselo3 = Kepviselo.builder().build();

        kepviseloRepository.saveAll(Set.of(kepviselo1, kepviselo2, kepviselo3));

    }
}
