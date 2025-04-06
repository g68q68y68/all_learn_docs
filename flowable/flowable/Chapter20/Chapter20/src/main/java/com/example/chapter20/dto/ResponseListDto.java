package com.example.chapter20.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class ResponseListDto<T> {

//    protected Integer pageSize;
//    protected Integer pageNumber;
    protected Long total;

    protected List<T> data;

    public ResponseListDto() {
        this.total = 0L;
        this.data = new ArrayList<>();
    }

}
