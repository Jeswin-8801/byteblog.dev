package com.jeswin8801.byteBlog.config;

import com.jeswin8801.byteBlog.util.WebUtil;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    private final ApplicationProperties properties;

    public ApplicationConfiguration(ApplicationProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public WebUtil getWebUtil() {
        return new WebUtil(
                properties.getOAuth2().getCookieExpireSeconds()
        );
    }
}
