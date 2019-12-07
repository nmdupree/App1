package com.lessons.models;

import java.util.List;

public class FilteredIndicatorInputDTO {

    private Integer pageSize;
    private Integer startingRecordNumber;
    private List<SortDTO> sorting;

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

    public List<SortDTO> getSorting() {
        return sorting;
    }

    public void setSorting(List<SortDTO> sorting) {
        this.sorting = sorting;
    }
}
