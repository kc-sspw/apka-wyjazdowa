package me.havooc.poligon.seed;

import lombok.Data;
import java.util.List;

@Data
public class ScheduleDaySeedDto {
    private String schedulePath; // TrainingPath enum name
    private String day; // ISO date, e.g. "2026-09-10"
    private List<ScheduleElementSeedDto> elements;
}