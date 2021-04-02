package com.heweixing.service;


import com.heweixing.pojo.Items;
import com.heweixing.pojo.ItemsImg;
import com.heweixing.pojo.ItemsParam;
import com.heweixing.pojo.ItemsSpec;
import com.heweixing.pojo.vo.CommentLevelCountsVO;
import com.heweixing.pojo.vo.ShopCartVO;
import com.heweixing.utils.PagedGridResult;

import java.util.List;

public interface ItemService {

    /**
     * 根据商品id查询详情
     * @param itemId
     * @return
     */
    public Items queryItemById(String itemId);

    /**
     * 根据商品id查询商品图片
     * @param itemId
     * @return
     */
    public List<ItemsImg> queryItemImgList(String itemId);

    /**
     * 根据商品id查询商品规格
     * @param itemId
     * @return
     */
    public List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品id查询商品参数
     * @param itemId
     * @return
     */
    public ItemsParam queryItemParam(String itemId);

    /**
     * 根据id查询商品的评价等级数量
     * @param itemId
     */
    public CommentLevelCountsVO queryCommentCounts(String itemId);

    /**
     * 这是根据商品id查询商品的评价(分页)
     * @param itemId
     * @param level
     * @return
     */
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize);

    /**
     * 搜索商品列表
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);

    /**
     * 搜索商品分类搜索商品列表
     * @param catId
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */

    public PagedGridResult searchItemsByThirdCat(Integer catId, String sort, Integer page, Integer pageSize);


    /**
     * 根据规格Ids查询最新的购物车商品数据(用于刷新渲染购物车的商品数据)
     * @param specIds
     * @return
     */
    public  List<ShopCartVO> queryItemsBySpecIds(String specIds);

    /**
     * 根据商品规格id获取规格对象的具体信息。
     * @param SpecId
     * @return
     */
    public ItemsSpec queryItemSpecById(String SpecId);

    /**
     * 根据商品id获取商品图片主图URL
     * @param itemId
     * @return
     */
    public String queryItemMainImgById(String itemId);

    /**
     *减少库存
     */
    public void decreaseItemSpecStock(String specId, int buyCount);

}
