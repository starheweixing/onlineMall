package com.heweixing.controller;

import com.heweixing.pojo.Users;
import com.heweixing.pojo.bo.UserBO;
import com.heweixing.service.UserService;
import com.heweixing.utils.CookieUtils;
import com.heweixing.utils.IMOOCJSONResult;
import com.heweixing.utils.JsonUtils;
import com.heweixing.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登录", tags = "用于注册登录的接口")
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    UserService userService;

    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username) {
        //1.判断入参是否为空
        if (StringUtils.isBlank(username)) {
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }

        //2.查找用户名是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(username);
        if (usernameIsExist) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }
        //3.请求成功，用户名不重复
        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirm = userBO.getConfirmPassword();

        //0.判断用户名和密码是否为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirm)) {
            return IMOOCJSONResult.errorMsg("用户名或者密码不能为空");
        }
        //1.查询用户是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(username);
        if (usernameIsExist) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }

        //2.判断长度不能少于6位
        if (password.length() < 6) {
            return IMOOCJSONResult.errorMsg("密码长度不能少于6位");
        }
        //3.判断两次密码是否一致
        if (!StringUtils.equals(password, confirm)) {
            return IMOOCJSONResult.errorMsg("两次密码不一致");
        }

        //4.实现注册
        Users userResult = userService.createUsers(userBO);
        // TODO 生成用户token，存入redis会话
        // TODO 同步购物车数据
        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult),true);


        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        //0.判断用户名和密码是否为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)
        ) {
            return IMOOCJSONResult.errorMsg("用户名或者密码不能为空");
        }


        //1.实现登录
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

        if (userResult == null) {
            return IMOOCJSONResult.errorMsg("用户名或者密码不正确");
        }

        userResult = setNullProperty(userResult);

        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult),true);

        // TODO 生成用户token，存入redis会话
        // TODO 同步购物车数据


        return IMOOCJSONResult.ok(userResult);
    }


    @ApiOperation(value = "用户登录退出登录", notes = "用户登录退出登录", httpMethod = "POST")
    @RequestMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId, HttpServletRequest request, HttpServletResponse response){

        //清除用户相关的信息
        CookieUtils.deleteCookie(request, response, "user");

        //TODO 用户退出登录，清除购物车
        //TODO 分布式会话中，清除用户信息
        return IMOOCJSONResult.ok();
    }




    public Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setCreatedTime(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setBirthday(null);
        userResult.setUpdatedTime(null);
        return userResult;
    }

}
