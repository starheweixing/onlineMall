package com.heweixing.service;

import com.heweixing.pojo.OrderStatus;
import com.heweixing.pojo.bo.ShopCartBO;
import com.heweixing.pojo.bo.SubmitOrderBO;
import com.heweixing.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {

    /**
     * 用于创建订单相关信息
     * @param submitOrderBO
     * @return
     */
    public OrderVO createOrder(List<ShopCartBO> shopCartBOList, SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    public OrderStatus queryOrderStatusInfo(String orderId);

    /**
     * 关闭超时未支付订单
     */
    public void closeOrder();

}
