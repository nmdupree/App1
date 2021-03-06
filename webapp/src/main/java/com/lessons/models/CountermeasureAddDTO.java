package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class CountermeasureAddDTO {

    private String value;

    private Integer status;

    @JsonProperty("start_date")
    private Timestamp startDate;

    @JsonProperty("end_date")
    private Timestamp endDate;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
}
