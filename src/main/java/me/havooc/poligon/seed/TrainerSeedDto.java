package me.havooc.poligon.seed;

import lombok.Data;

@Data
public class TrainerSeedDto {
    private String name;
    private String path; // matches TrainingPath enum name
    private String description;
    private String imageUrl;
    private String trainings;
    private Double imageAlignmentY;
}