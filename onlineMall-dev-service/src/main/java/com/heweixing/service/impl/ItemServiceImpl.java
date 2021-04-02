package com.heweixing.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.heweixing.enums.CommentLevel;
import com.heweixing.enums.YesOrNo;
import com.heweixing.mapper.*;
import com.heweixing.pojo.*;
import com.heweixing.pojo.vo.CommentLevelCountsVO;
import com.heweixing.pojo.vo.ItemCommentVO;
import com.heweixing.pojo.vo.SearchItemsVO;
import com.heweixing.pojo.vo.ShopCartVO;
import com.heweixing.service.ItemService;
import com.heweixing.utils.DesensitizationUtil;
import com.heweixing.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private ItemsImgMapper itemsImgMapper;

    @Autowired
    private ItemsSpecMapper itemsSpecMapper;

    @Autowired
    private ItemsParamMapper itemsParamMapper;

    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Autowired
    private ItemsMapperCustom itemsMapperCustom;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {

        Example example = new Example(ItemsImg.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemId", itemId);
        List<ItemsImg> result = itemsImgMapper.selectByExample(example);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example example = new Example(ItemsSpec.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemId", itemId);
        List<ItemsSpec> result = itemsSpecMapper.selectByExample(example);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParam(String itemId) {
        Example example = new Example(ItemsParam.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemId", itemId);
        ItemsParam result = itemsParamMapper.selectOneByExample(example);
        return result;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountsVO queryCommentCounts(String itemId) {

//      Integer totalCounts = getCommentCounts(, );
        Integer goodCounts = getCommentCounts(itemId, CommentLevel.GOOD.level);
        Integer normalCounts = getCommentCounts(itemId, CommentLevel.NORMAL.level);
        Integer badCounts = getCommentCounts(itemId, CommentLevel.BAD.level);
        Integer totalCounts = goodCounts + normalCounts + badCounts;

        CommentLevelCountsVO countsVO = new CommentLevelCountsVO();
        countsVO.setTotalCounts(totalCounts);
        countsVO.setGoodCounts(goodCounts);
        countsVO.setNormalCounts(normalCounts);
        countsVO.setBadCounts(badCounts);

        return countsVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer getCommentCounts(String itemId, Integer level) {

        ItemsComments condition = new ItemsComments();
        condition.setItemId(itemId);
        if (level != null) {
            condition.setCommentLevel(level);
        }

        Integer count = itemsCommentsMapper.selectCount(condition);
        return count;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("level", level);

        /**
         *  page: 第几页
         *  pageSize: 每页显示条数
         */
        PageHelper.startPage(page, pageSize);
        List<ItemCommentVO> list = itemsMapperCustom.queryItemComments(map);
        for (ItemCommentVO vo : list) {
            vo.setNickname(DesensitizationUtil.commonDisplay(vo.getNickname()));
        }

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

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);
        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItems(map);
        return setterPageGrid(list, page);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItemsByThirdCat(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);
        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItemsByThirdCat(map);
        return setterPageGrid(list, page);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopCartVO> queryItemsBySpecIds(String specIds) {

        String[] ids = specIds.split(",");
        List<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList, ids);
        List<ShopCartVO> result = itemsMapperCustom.queryItemsBySpecIds(specIdsList);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsSpec queryItemSpecById(String SpecId) {
        return itemsSpecMapper.selectByPrimaryKey(SpecId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemMainImgById(String itemId) {

        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNo.yes.type);
        ItemsImg result = itemsImgMapper.selectOne(itemsImg);
        return result != null ? result.getUrl() : "";
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void decreaseItemSpecStock(String specId, int buyCount) {

        //synchronized不推荐，集群下无用,性能低下
        //锁数据库：不推荐，导致数据库性能低下
        //分布式锁 zookeeper 和redis 可行
        int result = itemsMapperCustom.decreaseItemSpecStock(specId, buyCount);
        if (result != 1) {
            throw new RuntimeException("订单创建失败, 原因: 库存不足");
        }
    }


}
