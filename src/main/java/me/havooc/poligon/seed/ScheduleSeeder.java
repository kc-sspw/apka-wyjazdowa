package me.havooc.poligon.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.havooc.poligon.model.ScheduleDay;
import me.havooc.poligon.model.ScheduleElement;
import me.havooc.poligon.model.TrainingPath;
import me.havooc.poligon.repository.ScheduleDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduleSeeder implements CommandLineRunner {

    private final ScheduleDayRepository scheduleDayRepository;

    @Override
    public void run(String... args) throws Exception {
        if (scheduleDayRepository.count() > 0) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        File externalFile = new File("/data/seed/schedule-seed.json");

        try (InputStream is = externalFile.exists()
                ? new FileInputStream(externalFile)
                : new ClassPathResource("schedule-seed.json").getInputStream()) {

            List<ScheduleDaySeedDto> seedList = mapper.readValue(is, mapper.getTypeFactory()
                    .constructCollectionType(List.class, ScheduleDaySeedDto.class));

            for (ScheduleDaySeedDto dto : seedList) {
                List<ScheduleElement> elements = dto.getElements().stream()
                        .map(e -> ScheduleElement.builder()
                                .startTime(LocalTime.parse(e.getStartTime()))
                                .endTime(LocalTime.parse(e.getEndTime()))
                                .description(e.getDescription())
                                .build())
                        .collect(Collectors.toList());

                scheduleDayRepository.save(ScheduleDay.builder()
                        .schedulePath(TrainingPath.valueOf(dto.getSchedulePath()))
                        .day(LocalDate.parse(dto.getDay()))
                        .elements(elements)
                        .lastModified(LocalDateTime.now())
                        .build());
            }

            System.out.println("Seeded " + seedList.size() + " schedule days from "
                    + (externalFile.exists() ? "external file" : "bundled resource"));
        }
    }
}