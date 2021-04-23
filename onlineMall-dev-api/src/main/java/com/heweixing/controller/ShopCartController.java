package com.heweixing.controller;

import com.heweixing.pojo.bo.ShopCartBO;
import com.heweixing.utils.IMOOCJSONResult;
import com.heweixing.utils.JsonUtils;
import com.heweixing.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Api(value = "购物车接口", tags = "购物车相关的api")
@RestController
@RequestMapping("shopcart")
public class ShopCartController extends BaseController {

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public IMOOCJSONResult add(@RequestParam String userId, @RequestBody ShopCartBO shopCartBO, HttpServletRequest request, HttpServletResponse response) {

        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("");
        }

        //前端用户在登录的情况下,添加商品到购物车，会同时在后端同步购物车到redis缓存
        //需要判断当前购物车中包含已经存在的商品，如果存在则累加购买数量
        String shopCartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        List<ShopCartBO> shopCartBOList = null;

        if (StringUtils.isNoneBlank(shopCartJson)) {
            shopCartBOList = JsonUtils.jsonToList(shopCartJson, ShopCartBO.class);
            //判断购物车中是否有商品,如果有的话count累加
            boolean isHaving = false;
            for (ShopCartBO sc : shopCartBOList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(shopCartBO.getSpecId())) {
                    sc.setBuyCounts(sc.getBuyCounts() + shopCartBO.getBuyCounts());
                    isHaving = true;
                }
            }

            if (!isHaving) {
                shopCartBOList.add(shopCartBO);
            }
        } else {
            //redis里面没有购物车
            shopCartBOList = new ArrayList<>();
            //直接添加到购物车
            shopCartBOList.add(shopCartBO);
        }
        redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopCartBOList));

        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    @PostMapping("/del")
    public IMOOCJSONResult del(@RequestParam String userId, @RequestParam String itemSpecId, HttpServletRequest request, HttpServletResponse response) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return IMOOCJSONResult.errorMsg("参数不能为空");
        }

        //用户在页面删除购物车中的商品数据,如果用户此时已经登陆,则需要同步删除redis后端购物车中的商品。
        String shopCartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        List<ShopCartBO> shopCartBOList = null;
        if (StringUtils.isNoneBlank(shopCartJson)) {
            shopCartBOList = JsonUtils.jsonToList(shopCartJson, ShopCartBO.class);
            //判断购物车中是否有商品,如果有的话count累加
//            Iterator iterator = shopCartBOList.iterator();
//            while(iterator.hasNext()){
//                ShopCartBO sc = (ShopCartBO) iterator.next();
//                String tmpSpecId = sc.getSpecId();
//            }
            for (ShopCartBO sc : shopCartBOList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(itemSpecId)) {
                    shopCartBOList.remove(sc);
                    break;
                }
            }
        }
        redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopCartBOList));
        return IMOOCJSONResult.ok();
    }

}
