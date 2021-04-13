package com.heweixing;

// 打包war[4] ,增加war的启动类

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class WarStarterApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //指向Application这个SpringBoot的启动类
        return builder.sources(Application.class);
    }
}
