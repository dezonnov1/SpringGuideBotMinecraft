package com.dezonnov1.SpringGuideBotMinecraft.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyJsonDto {

    private String parameter;

    // В JSON это "values", а в Java мы хотим "possibleValues"
    @JsonProperty("values")
    private String possibleValues;

    // В JSON это "on_default", а в Java "defaultValue"
    @JsonProperty("on_default")
    private String defaultValue;

    private String description;

    private String recommendation;

    private List<String> versions;
}