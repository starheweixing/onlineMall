package com.heweixing.controller;

import com.heweixing.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

//@Controller
@ApiIgnore
@RestController  //返回的都是json对象
public class StufooController {
    @Autowired
    StuService stuService;

    @GetMapping("/getStu")
    public Object getStu(Integer id) {
        return stuService.getStuInfo(id);
    }


    @PostMapping("/saveStu")
    public Object saveStu(){
        stuService.saveStu();
        return "OK";
    }

    @PostMapping("/updateStu")
    public Object updateStu(Integer id){
        stuService.updateStu(id);
        return "OK";
    }

    @PostMapping("/DeleteStu")
    public Object deleteStu(Integer id){
        stuService.deleteStu(id);
        return "OK";
    }



}
