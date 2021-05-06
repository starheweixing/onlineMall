package com.heweixing.controller;

import com.heweixing.pojo.Users;
import com.heweixing.pojo.bo.ShopCartBO;
import com.heweixing.pojo.bo.UserBO;
import com.heweixing.pojo.vo.UsersVO;
import com.heweixing.service.UserService;
import com.heweixing.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.JumpStatement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Api(value = "注册登录", tags = "用于注册登录的接口")
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    @Autowired
    UserService userService;

    @Autowired
    private RedisOperator redisOperator;

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
//        userResult = setNullProperty(userResult);

        UsersVO usersVO = convertUsersVO(userResult);

        // 同步购物车数据
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);

        synShopCartData(userResult.getId(), request, response);
        return IMOOCJSONResult.ok();
    }

    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     */
    private void synShopCartData(String userId, HttpServletRequest request, HttpServletResponse response) {
        /**
         * 1.redis中没有数据，如果cookie中的购物从为空，不做任何处理
         *                  cookie这不为空，直接放入redis
         * 2. redis中有数据,如果cookie为空，直接将redis覆盖本地cookie
         *                 如果cookie中的购物车不为空,则以cookie为主,删除redis，把cookie中商品直接覆盖redis(参考京东)
         *
         * 3. 同步到redis中之后，覆盖本地cookie购物车的数据，保证本地购物车的数据是同步最新的
         */

        //从redis中获取购物车
        String shopCartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);

        //从cookie中获取购物车
        String shopCartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);

        if (StringUtils.isBlank(shopCartJsonRedis)) {
            //redis为空，cookie不为空
            if (StringUtils.isNoneBlank(shopCartStrCookie)) {
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopCartStrCookie);
            }
        } else {
            //redis不为空,cookie不为空，合并cookie和redis中购物车的商品数据(同一商品则覆盖数据 )
            if (StringUtils.isNoneBlank(shopCartStrCookie)) {
                /**
                 * 1.已经存在的, 把cookie中对应的数量，覆盖redis
                 * 2.该项商品标记为待删除，同意放入待删除的list中
                 * 3.从cookie中清理所有的待删除list
                 * 4.合并redis和cookie中的数据
                 * 5.更新redis和cookie
                 */
                List<ShopCartBO> shopCartListRedis = JsonUtils.jsonToList(shopCartJsonRedis, ShopCartBO.class);
                List<ShopCartBO> shopCartListCookie = JsonUtils.jsonToList(shopCartStrCookie, ShopCartBO.class);

                List<ShopCartBO> pendingDeleteList = new ArrayList<>();

                Map<String, ShopCartBO> shopCartListCookieIdsMap = new HashMap<>();
                for (ShopCartBO cookieShopCart : shopCartListCookie) {
                    shopCartListCookieIdsMap.put(cookieShopCart.getSpecId(), cookieShopCart);
                }


                for (ShopCartBO redisShopCart : shopCartListRedis) {
                    String redisSpecId = redisShopCart.getSpecId();
                    if (shopCartListCookieIdsMap.containsKey(redisSpecId)) {
                        //覆盖购买数量，不累加,参考京东
                        redisShopCart.setBuyCounts(shopCartListCookieIdsMap.get(redisSpecId).getBuyCounts());
                        //把cookieShopCart放入待删除队列，最后删除与合并
                        pendingDeleteList.add(shopCartListCookieIdsMap.get(redisSpecId));
                        shopCartListCookieIdsMap.remove(redisSpecId);
                    }
                }
                //从现有cookie中删除对应的覆盖过的商品数据
                shopCartListCookie.removeAll(pendingDeleteList);

                //合并两个list
                shopCartListRedis.addAll(shopCartListCookie);
                //更新到redis和cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopCartListRedis), true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopCartListRedis));
            } else {
                //redis不为空,cookie为空
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopCartJsonRedis, true);
            }
        }


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

        // userResult = setNullProperty(userResult);
        UsersVO usersVO = convertUsersVO(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);
        //  生成用户token，存入redis会话
        //  同步购物车数据
        synShopCartData(userResult.getId(), request, response);

        return IMOOCJSONResult.ok(userResult);
    }


    @ApiOperation(value = "用户登录退出登录", notes = "用户登录退出登录", httpMethod = "POST")
    @RequestMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId, HttpServletRequest request, HttpServletResponse response) {

        //清除用户相关的信息
        CookieUtils.deleteCookie(request, response, "user");

        //用户退出登录，清除redis中user的会话信息
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        //分布式会话中，清除用户信息,购物车
        CookieUtils.deleteCookie(request, response, FOODIE_SHOPCART);
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
