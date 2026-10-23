package com.team1.deskapi.desk.config;

import com.team1.deskapi.desk.web.pagination.SafePageableResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SafePageableResolver safePageableResolver;

    @Override
    public void addArgumentResolvers(
            List<HandlerMethodArgumentResolver> resolvers
    ) {
        resolvers.addFirst(safePageableResolver);
    }

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .usePathSegment(0)
                .addSupportedVersions("1");
    }
}
