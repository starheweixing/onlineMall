package com.heweixing.service.impl.center;

import com.github.pagehelper.PageInfo;
import com.heweixing.utils.PagedGridResult;

import java.util.List;

public class BaseService {

    public PagedGridResult setterPageGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);                     // 当前页数
        grid.setRows(list);                     // 当前页数
        grid.setTotal(pageList.getPages());     //每行显示的内容
        grid.setRecords(pageList.getTotal());   //总记录数
        return grid;
    }
}
