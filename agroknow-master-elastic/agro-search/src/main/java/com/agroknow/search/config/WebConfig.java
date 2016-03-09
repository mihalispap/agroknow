package com.agroknow.search.config;

import com.agroknow.domain.parser.json.CustomObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * @author aggelos
 */
@Configuration
@ImportResource("classpath:/com/agroknow/search/config/security/securityConfig.xml")
@ComponentScan({ "com.agroknow.search.web.controllers", "com.agroknow.search.web.security" })
public class WebConfig extends WebMvcConfigurationSupport {

    public static final int UPLOAD_MAX_SIZE = 10485760;

    @Autowired
    private CustomObjectMapper objectMapper;

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setOrder(2);
        return viewResolver;
    }

    @Bean
    public BeanNameViewResolver beanNameViewResolver() {
        BeanNameViewResolver resolver = new BeanNameViewResolver();
        resolver.setOrder(1);
        return resolver;
    }

    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping handlerMapping = super.requestMappingHandlerMapping();
        handlerMapping.setUseTrailingSlashMatch(false);
        handlerMapping.setUseSuffixPatternMatch(false);
        return handlerMapping;
    }

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setObjectMapper(objectMapper);
        converters.add(jacksonConverter);
    }
}
