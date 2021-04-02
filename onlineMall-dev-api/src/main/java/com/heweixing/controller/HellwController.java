package com.heweixing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

//@Controller
@ApiIgnore
@RestController  //返回的都是json对象
public class HellwController {

    @GetMapping("/hello")
    public Object hello(){
        return "helloWord";
    }



}
