package com.example.chapter20.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NewModelDto {
    @JsonProperty("model_name")
    String modelName;
    @JsonProperty("model_key")
    String modelKey;
    String description;
}
