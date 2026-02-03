package io.cjc.backend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int page;
    private int pageSize;
    private int pageCount;
    private long itemCount;
    private List<T> list;

    public static <T> PageResponse<T> of(int page, int pageSize, long total, List<T> list) {
        int pageCount = (int) Math.ceil((double) total / pageSize);
        return new PageResponse<>(page, pageSize, pageCount, total, list);
    }
}
