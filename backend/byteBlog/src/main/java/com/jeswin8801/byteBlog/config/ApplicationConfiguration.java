package com.jeswin8801.byteBlog.config;

import com.jeswin8801.byteBlog.entities.dto.blog.BlogsCompactResponseDto;
import com.jeswin8801.byteBlog.entities.model.Blog;
import com.jeswin8801.byteBlog.util.WebUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfiguration {

    @Autowired
    private ApplicationProperties properties;

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

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(8);
    }
}
