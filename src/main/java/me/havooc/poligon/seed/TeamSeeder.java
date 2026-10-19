package me.havooc.poligon.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.havooc.poligon.model.Team;
import me.havooc.poligon.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamSeeder implements CommandLineRunner {
    private final TeamRepository teamRepository;

    @Override
    public void run(String... args) throws Exception {
        //teamRepository.deleteAll();

        ObjectMapper mapper = new ObjectMapper();
        File externalFile = new File("/data/seed/team-seed.json");

        try (InputStream is = externalFile.exists()
                ? new FileInputStream(externalFile)
                : new ClassPathResource("team-seed.json").getInputStream()) {

            List<TeamSeedDto> seedList = mapper.readValue(is, mapper.getTypeFactory()
                    .constructCollectionType(List.class, TeamSeedDto.class));

            for (TeamSeedDto dto : seedList) {
                teamRepository.save(Team.builder()
                        .name(dto.getName())
                        .captainName(dto.getCaptainName())
                        .members(dto.getMembers())
                        .imageUrl(dto.getImageUrl())
                        .points(dto.getPoints())
                        .lastModified(LocalDateTime.now())
                        .build());
            }

            System.out.println("Seeded " + seedList.size() + " teams from "
                    + (externalFile.exists() ? "external file" : "bundled resource"));
        }
    }
}