package com.heweixing.controller.center;

import com.heweixing.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

//@Controller
@ApiIgnore
@RestController  //返回的都是json对象
@RequestMapping("redis")
public class RedisController {
    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public Object set(@RequestParam String key, @RequestParam String value) {
        redisOperator.set(key, value);
        return "OK";
    }

    @GetMapping("/get")
    public Object get(@RequestParam String key) {
        Object value = redisOperator.get(key);
        return (String) value;
    }

    @GetMapping("/delete")
    public Object delete(@RequestParam String key) {
        redisOperator.del(key);
        return "OK";
    }

}
