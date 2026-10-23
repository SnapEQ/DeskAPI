package com.team1.deskapi.desk.web.pagination;

import com.team1.deskapi.desk.exceptions.InvalidRequestException;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@Component
public class SafePageableResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SafePageable.class)
                && Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Pageable resolveArgument(
            MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest request,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        SafePageable config = Objects.requireNonNull(
                parameter.getParameterAnnotation(SafePageable.class),
                "SafePageable annotation is required"
        );

        if (request.getParameter("sort") != null) {
            throw new InvalidRequestException("Sorting is not supported");
        }

        int page = readInt(request, "page", 0, 0, Integer.MAX_VALUE);
        int size = readInt(
                request,
                "size",
                config.defaultSize(),
                1,
                config.maxSize()
        );

        Sort sort = Sort.by(config.defaultSortDirection(), config.sortBy());

        return PageRequest.of(page, size, sort);
    }

    private int readInt(
            NativeWebRequest request,
            String name,
            int defaultValue,
            int min,
            int max
    ) {
        String value = request.getParameter(name);

        if (value == null) {
            return defaultValue;
        }

        try {
            int number = Integer.parseInt(value);

            if (number < min || number > max) {
                throw new InvalidRequestException(
                        name + " must be between " + min + " and " + max
                );
            }

            return number;
        } catch (NumberFormatException exception) {
            throw new InvalidRequestException(name + " must be a number");
        }
    }
}
