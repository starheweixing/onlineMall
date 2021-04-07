package com.heweixing.service.center;

import com.heweixing.pojo.Orders;
import com.heweixing.utils.PagedGridResult;

public interface MyOrdersService  {
    /**
     * 查询我的订单列表.
     * @param userId
     * @param orderStatus
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize);

    /**
     * 订单状态 ->商家发货
     * @param orderId
     */
    public void updateDeliverOrderStatus(String orderId);

    /**
     *查询我的订单
     */
    public Orders queryMyOrder(String orderId, String userId);

    /**
     * 更新订单状态，确认收货
     * @param orderId
     * @return
     */
    public boolean updateReceiveOrderStatus(String orderId);

    /**
     * 删除订单,逻辑删除
     * @param userId
     * @param orderId
     * @return
     */
    public boolean deleteOrder(String userId, String orderId);


}
