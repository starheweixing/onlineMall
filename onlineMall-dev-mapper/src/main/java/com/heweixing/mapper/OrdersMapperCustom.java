package com.heweixing.mapper;

import com.heweixing.pojo.OrderStatus;
import com.heweixing.pojo.vo.MyOrdersVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface OrdersMapperCustom {

     List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String,Object> map);

     public int getMyOrderStatusCounts(@Param("paramsMap") Map<String,Object> map);

     public List<OrderStatus> getMyOrderTrend(@Param("paramsMap") Map<String,Object> map);

}