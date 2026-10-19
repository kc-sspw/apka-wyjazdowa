package me.havooc.poligon.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.havooc.poligon.model.Trainer;
import me.havooc.poligon.model.TrainingPath;
import me.havooc.poligon.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrainerSeeder implements CommandLineRunner {

    private final TrainerRepository trainerRepository;

    @Override
    public void run(String... args) throws Exception {
        if (trainerRepository.count() > 0) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        File externalFile = new File("/data/seed/trainer-seed.json");

        try (InputStream is = externalFile.exists()
                ? new FileInputStream(externalFile)
                : new ClassPathResource("trainer-seed.json").getInputStream()) {

            List<TrainerSeedDto> seedList = mapper.readValue(is, mapper.getTypeFactory()
                    .constructCollectionType(List.class, TrainerSeedDto.class));

            for (TrainerSeedDto dto : seedList) {
                trainerRepository.save(Trainer.builder()
                        .name(dto.getName())
                        .path(TrainingPath.valueOf(dto.getPath()))
                        .description(dto.getDescription())
                        .imageUrl(dto.getImageUrl())
                        .trainings(dto.getTrainings())
                        .lastModified(LocalDateTime.now())
                        .imageAlignmentY(dto.getImageAlignmentY())
                        .build());
            }

            System.out.println("Seeded " + seedList.size() + " trainers from "
                    + (externalFile.exists() ? "external file" : "bundled resource"));
        }
    }
}