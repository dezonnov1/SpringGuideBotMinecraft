package com.dezonnov1.SpringGuideBotMinecraft.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

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
