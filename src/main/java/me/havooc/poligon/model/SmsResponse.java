package me.havooc.poligon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmsResponse {
    private Integer error;
    private String message;
    private Integer count;
}