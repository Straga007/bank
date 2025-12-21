package com.bank.config.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ConfigListController {

    @Autowired
    private ConfigurableEnvironment environment;

    @GetMapping(value = "/configs", produces = MediaType.TEXT_HTML_VALUE)
    public String listConfigs() throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n<title>Available Configurations</title>\n</head>\n<body>\n");
        html.append("<h1>Available Service Configurations</h1>\n<ul>\n");
        
        // путь к директории с конфигурациями
        String searchLocation = environment.getProperty("spring.cloud.config.server.native.search-locations");
        
        if (searchLocation != null && searchLocation.startsWith("classpath:/")) {
            String resourcePath = searchLocation.substring("classpath:/".length());
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:/" + resourcePath + "/*.yml");
            
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null && filename.endsWith(".yml")) {
                    String serviceName = filename.substring(0, filename.length() - 4); // - .yml
                    html.append("<li><a href='/").append(serviceName).append("/default'>")
                        .append(serviceName).append("</a></li>\n");
                }
            }
        }
        
        html.append("</ul>\n</body>\n</html>");
        return html.toString();
    }
}