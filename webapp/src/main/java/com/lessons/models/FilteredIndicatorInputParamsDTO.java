package com.lessons.models;

import java.util.List;

public class FilteredIndicatorInputParamsDTO {

    private Integer pageSize;
    private Integer startingRecordNumber;
    private List<String> sorting;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getStartingRecordNumber() {
        return startingRecordNumber;
    }

    public void setStartingRecordNumber(Integer startingRecordNumber) {
        this.startingRecordNumber = startingRecordNumber;
    }

    public List<String> getSorting() {
        return sorting;
    }

    public void setSorting(List<String> sorting) {
        this.sorting = sorting;
    }
}
