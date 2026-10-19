package me.havooc.poligon.seed;

import lombok.Data;

@Data
public class CrewSeedDto {
    private String name;
    private String room;
    private String role;
    private String email;
    private String sobrietyDay;
    private String description;
    private String crewQuest;
    private String phoneNumber;
    private boolean isSuperAdmin;
    private boolean isHidden;
    private String imageUrl;
    private double imageAlignmentY;
}