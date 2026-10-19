package me.havooc.poligon.seed;

import lombok.Data;
import java.util.List;

@Data
public class TeamSeedDto {
    private String name;
    private String captainName;
    private List<String> members;
    private String imageUrl;
    private int points;
}