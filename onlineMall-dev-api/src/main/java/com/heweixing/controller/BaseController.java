package com.heweixing.controller;

import com.heweixing.pojo.Orders;
import com.heweixing.service.center.MyOrdersService;
import com.heweixing.utils.IMOOCJSONResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class BaseController {

    public static final Integer COMMON_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;

    public static final String FOODIE_SHOPCART = "shopcart";

    @Autowired
    public MyOrdersService myOrdersService;


    //支付中心的调用地址
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";


    //微信支付成功->支付中心->天天吃货平台
    //                  ->回调通知的url
    // public static final String payReturnUrl = "http://localhost:8088/orders/notifyMerchantOrderPaid"; //开发环境
    public static final String payReturnUrl = "http://121.4.122.91:8088/foodie-dev-api/orders/notifyMerchantOrderPaid";  //生产环境
    //头像保存地址。
    public static final String IMAGE_USER_FACE_LOCATION = "D:" + File.separator + "imageWorkSpace";


    /**
     * 用于验证用户和订单是否有关联关系,避免用户非法调用
     *
     * @return
     */
    public IMOOCJSONResult checkUserOrder(String orderId, String userId) {
        Orders orders = myOrdersService.queryMyOrder(orderId, userId);
        if (orderId == null) {
            return IMOOCJSONResult.errorMsg("订单不存在");
        }
        return IMOOCJSONResult.ok(orders);
    }

}
