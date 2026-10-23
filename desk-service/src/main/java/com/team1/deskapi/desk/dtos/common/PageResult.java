package com.team1.deskapi.desk.dtos.common;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(
        List<T> content,
        int pageNumber,
        int size,
        boolean hasNext
) {

    public PageResult {
        content = List.copyOf(content);
    }

    public static <T> PageResult<T> from(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.hasNext()
        );
    }

    public <R> PageResult<R> map(
            Function<? super T, ? extends R> mapper
    ) {
        return new PageResult<>(
                content.stream()
                        .<R>map(mapper)
                        .toList(),
                pageNumber,
                size,
                hasNext
        );
    }
}
