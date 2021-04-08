package com.heweixing.service.impl.center;


import com.github.pagehelper.PageHelper;
import com.heweixing.enums.YesOrNo;
import com.heweixing.mapper.ItemsCommentsMapperCustom;
import com.heweixing.mapper.OrderItemsMapper;
import com.heweixing.mapper.OrderStatusMapper;
import com.heweixing.mapper.OrdersMapper;
import com.heweixing.pojo.OrderItems;
import com.heweixing.pojo.OrderStatus;
import com.heweixing.pojo.Orders;
import com.heweixing.pojo.bo.center.OrderItemsCommentBO;
import com.heweixing.pojo.vo.MyCommentVO;
import com.heweixing.service.center.MyCommentsService;
import com.heweixing.utils.PagedGridResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyCommentsServiceImpl extends BaseService implements MyCommentsService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {

        OrderItems query = new OrderItems();
        query.setOrderId(orderId);

        List<OrderItems> orderItems = orderItemsMapper.select(query);
        return orderItems;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComments(String orderId, String userId, List<OrderItemsCommentBO> commentList) {

        //1.保存评价 items_comments
        for(OrderItemsCommentBO oic : commentList){
            oic.setCommentId(sid.nextShort());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("commentList", commentList);
        itemsCommentsMapperCustom.saveComments(map);

        //2.修改订单表已评价 orders
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setIsComment(YesOrNo.yes.type);
        ordersMapper.updateByPrimaryKeySelective(orders);

        //3.修改订单状态表的留言时间 order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyComment(String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        PageHelper.startPage(page,pageSize);
        List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);

        PagedGridResult grid = setterPageGrid(list, page);
        return grid;
    }
}
