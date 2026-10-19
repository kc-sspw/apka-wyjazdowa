package me.havooc.poligon.seed;

import lombok.Data;

@Data
public class ScheduleElementSeedDto {
    private String startTime; // "HH:mm:ss"
    private String endTime;
    private String description;
}