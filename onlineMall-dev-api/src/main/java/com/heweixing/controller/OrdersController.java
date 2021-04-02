package com.heweixing.controller;

import com.heweixing.enums.OrderStatusEnum;
import com.heweixing.enums.PayMethod;
import com.heweixing.pojo.OrderStatus;
import com.heweixing.pojo.bo.SubmitOrderBO;
import com.heweixing.pojo.vo.MerchantOrdersVO;
import com.heweixing.pojo.vo.OrderVO;
import com.heweixing.service.OrderService;
import com.heweixing.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Api(value = "订单相关", tags = "订单相关的api接口")
@RestController
@RequestMapping("orders")
public class OrdersController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;


    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO, HttpServletRequest request, HttpServletResponse response) {

        if (submitOrderBO.getPayMethod() != PayMethod.WEIXIN.type && submitOrderBO.getPayMethod() != PayMethod.ALIPAY.type) {
            return IMOOCJSONResult.errorMsg("支付方式不支持");
        }

        System.out.println(submitOrderBO.toString());
        //1. 创建订单
        OrderVO orderVO = orderService.createOrder(submitOrderBO);
        String orderId = orderVO.getOrderId();


        //2. 创建订单以后，移除购物车已结算或者已提交的商品.
        //TODO 整合redis之后完善购物车中的已结算商品清除，并同步到前端的cookie
//        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, "", true);

        //3. 向支付中心发送当前订单, 用于保存支付中心的订单数据.
        MerchantOrdersVO merchantOrdersVO =  orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);


        merchantOrdersVO.setAmount(1); //为了方便测试，所有的购买统一改成一分钱

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId", "imooc");
        headers.add("password", "imooc");
        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO, headers);

        ResponseEntity<IMOOCJSONResult> responseEntity = restTemplate.postForEntity(paymentUrl, entity, IMOOCJSONResult.class);
        IMOOCJSONResult paymentResult = responseEntity.getBody();
        if(paymentResult.getStatus() != 200){
            return IMOOCJSONResult.errorMsg("支付中心创建订单失败，请联系管理员");
        }

        return IMOOCJSONResult.ok(orderId);
    }

    @PostMapping("/notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId){

        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @PostMapping("/getPaidOrderInfo")
    public IMOOCJSONResult getPaidOrderInfo(String orderId){

        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return IMOOCJSONResult.ok(orderStatus);
    }

}
