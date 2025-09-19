package com.mipa.auth.Security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${data.settings.avatars.srcDir}")
    private String avatarsSrcDir ;

    @Value("${data.settings.avatars.dstDir}")
    private String avatarsDstDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(avatarsSrcDir+"/**")
                .addResourceLocations("file:/"+avatarsDstDir)
                .setCachePeriod(3600);
    }
}
