package com.lessons.models;

public class UpdateReportDTO {

    private int id;
    private String description;
    private int version;
    private Integer priority;
    private String display_name;
    private Integer reference_source;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public Integer getReference_source() {
        return reference_source;
    }

    public void setReference_source(Integer reference_source) {
        this.reference_source = reference_source;
    }
}
