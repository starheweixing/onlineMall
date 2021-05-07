package com.heweixing.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


//@Controller
@ApiIgnore
@RestController  //返回的都是json对象
public class HellwController {

    final static Logger logger = LoggerFactory.getLogger(HellwController.class);

    @GetMapping("/hello")
    public Object hello(){

        logger.debug("debug: hello---");
        logger.info("info: hello---");
        logger.warn("warn: hello---");
        logger.error("error: hello---");

        return "hello Word";
    }


    @GetMapping("/setSession")
    public Object setSession(HttpServletRequest request){

        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("userInfo", "new user");
        httpSession.setMaxInactiveInterval(36000);
        httpSession.getAttribute("userInfo");
        return "ok";
    }





}
