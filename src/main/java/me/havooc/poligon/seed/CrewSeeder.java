package me.havooc.poligon.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.havooc.poligon.model.Crew;
import me.havooc.poligon.repository.CrewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CrewSeeder implements CommandLineRunner {
    private final CrewRepository crewRepository;

    @Override
    public void run(String... args) throws Exception {
        // Usuń istniejące dane przed ponownym seedowaniem
        //crewRepository.deleteAll();

        ObjectMapper mapper = new ObjectMapper();
        File externalFile = new File("/data/seed/crew-seed.json");
        try (InputStream is = externalFile.exists()
                ? new FileInputStream(externalFile)
                : new ClassPathResource("crew-seed.json").getInputStream()) {
            List<CrewSeedDto> seedList = mapper.readValue(is, mapper.getTypeFactory()
                    .constructCollectionType(List.class, CrewSeedDto.class));
            for (CrewSeedDto dto : seedList) {
                crewRepository.save(Crew.builder()
                        .name(dto.getName())
                        .room(dto.getRoom())
                        .role(dto.getRole())
                        .email(dto.getEmail())
                        .sobrietyDay(LocalDate.parse(dto.getSobrietyDay()))
                        .description(dto.getDescription())
                        .crewQuest(dto.getCrewQuest())
                        .phoneNumber(dto.getPhoneNumber())
                        .isSuperAdmin(dto.isSuperAdmin())
                        .isHidden(dto.isHidden())
                        .imageUrl(dto.getImageUrl())
                        .lastModified(LocalDateTime.now())
                        .imageAlignmentY(dto.getImageAlignmentY())
                        .build());
            }
            System.out.println("Seeded " + seedList.size() + " crew members from "
                    + (externalFile.exists() ? "external file" : "bundled resource"));
        }
    }
}