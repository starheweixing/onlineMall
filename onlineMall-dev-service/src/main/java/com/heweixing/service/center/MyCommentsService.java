package com.heweixing.service.center;

import com.heweixing.pojo.OrderItems;
import com.heweixing.pojo.bo.center.OrderItemsCommentBO;
import com.heweixing.utils.PagedGridResult;

import java.util.List;

public interface MyCommentsService {
    /**
     * 根据订单id查询关联的商品
     * @param orderId
     * @return
     */
    public List<OrderItems> queryPendingComment(String orderId);

    /**
     *  保存用户的评论
     * @param orderId
     * @param userId
     * @param commentList
     */
    public void saveComments(String orderId, String userId, List<OrderItemsCommentBO> commentList);

    /**
     * 我的评价查询分页
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyComment(String userId, Integer page, Integer pageSize);


}
