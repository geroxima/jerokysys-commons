package com.jerokysys.commons.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResult<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
}
