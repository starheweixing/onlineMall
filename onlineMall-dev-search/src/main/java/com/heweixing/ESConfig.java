package com.heweixing;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ESConfig {

    @PostConstruct
    void init(){
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

}
