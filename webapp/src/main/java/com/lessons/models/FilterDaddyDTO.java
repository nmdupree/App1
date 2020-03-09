package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FilterDaddyDTO {

    @JsonProperty("pagesize")
    private Integer pageSize;

    private Integer offset;

    private List<FilterBabyDTO> filters;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<FilterBabyDTO> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterBabyDTO> filters) {
        this.filters = filters;
    }
}
