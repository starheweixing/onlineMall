package com.heweixing.service.impl.center;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.heweixing.mapper.OrdersMapperCustom;
import com.heweixing.pojo.vo.MyOrdersVO;
import com.heweixing.service.center.MyOrdersService;
import com.heweixing.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyOrdersServiceImpl implements MyOrdersService {

    @Autowired
    public OrdersMapperCustom ordersMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        if(orderStatus != null){
            map.put("orderStatus", orderStatus);
        }
        PageHelper.startPage(page,pageSize);
        List<MyOrdersVO> list = ordersMapperCustom.queryMyOrders(map);
        return setterPageGrid(list, page);
    }

    private PagedGridResult setterPageGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);                     // 当前页数
        grid.setRows(list);                     // 当前页数
        grid.setTotal(pageList.getPages());     //每行显示的内容
        grid.setRecords(pageList.getTotal());   //总记录数
        return grid;
    }

}
