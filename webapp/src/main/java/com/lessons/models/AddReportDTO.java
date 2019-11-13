package com.lessons.models;

public class AddReportDTO {

    private String description;
    private Boolean reviewed;
    private Integer priority;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getReviewed() {
        return reviewed;
    }

    public void setReviewed(Boolean reviewed) {
        this.reviewed = reviewed;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
