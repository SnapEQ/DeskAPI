package com.team1.deskapi.desk.web.pagination;

import org.springframework.data.domain.Sort;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SafePageable {

    int defaultSize() default 20;

    int maxSize() default 100;

    String[] sortBy() default {"createdAt", "id"};

    Sort.Direction defaultSortDirection() default Sort.Direction.DESC;
}
