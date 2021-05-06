package com.heweixing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//@Controller
@ApiIgnore
@RestController  //返回的都是json对象
public class HellwController {

    @GetMapping("/hello")
    public Object hello(){
        return "helloWord";
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
