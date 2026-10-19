package me.havooc.poligon.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import  me.havooc.poligon.model.Song;
import  me.havooc.poligon.repository.SongRepository;
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
public class SongSeeder implements CommandLineRunner {

    private final SongRepository songRepository;

    @Override
    public void run(String... args) throws Exception {
        if (songRepository.count() > 0) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        File externalFile = new File("/data/seed/song-seed.json");

        try (InputStream is = externalFile.exists()
                ? new FileInputStream(externalFile)
                : new ClassPathResource("song-seed.json").getInputStream()) {

            List<SongSeedDto> seedList = mapper.readValue(is, mapper.getTypeFactory()
                    .constructCollectionType(List.class, SongSeedDto.class));

            for (SongSeedDto dto : seedList) {
                songRepository.save(Song.builder()
                        .title(dto.getTitle())
                        .songText(dto.getSongText())
                        .lastModified(LocalDateTime.now())
                        .build());
            }

            System.out.println("Seeded " + seedList.size() + " songs from "
                    + (externalFile.exists() ? "external file" : "bundled resource"));
        }
    }
}