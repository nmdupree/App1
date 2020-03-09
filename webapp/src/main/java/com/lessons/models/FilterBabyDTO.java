package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FilterBabyDTO {

    private String type;

    @JsonProperty("field_name")
    private String fieldName;

    private String direction;

    @JsonProperty("field_values")
    private List<String> fieldValues;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public List<String> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(List<String> fieldValues) {
        this.fieldValues = fieldValues;
    }
}
