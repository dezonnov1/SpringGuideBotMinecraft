package com.dezonnov1.SpringGuideBotMinecraft.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * dto для преобразования json в записи в бд
 * Читает данные об server.properties
 * (значения по умолчанию, описание, рекомендации и тд ) читает из json
  */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyJsonDto {

    private String parameter;

    @JsonProperty("values")
    private String possibleValues;

    @JsonProperty("on_default")
    private String defaultValue;

    private String description;

    private String recommendation;

    private List<String> versions;
}
