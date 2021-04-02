package com.heweixing.service;

import com.heweixing.pojo.Carousel;
import com.heweixing.pojo.Category;
import com.heweixing.pojo.vo.CategoryVO;
import com.heweixing.pojo.vo.NewItemsVO;

import java.util.List;

public interface CategoryService {
    /**
     * 查询所有一级分类
     * @return
     */
   public List<Category> queryAllRootLevelCat();

    /**
     * 根据一级分类Id查询子分类
     * @param rootCatId
     * @return
     */
   public List<CategoryVO> getSubCatList(Integer rootCatId);

    /**
     * 查询首页每个一级分类下的6条最新商品数据
     * @param rootCatId
     * @return
     */
   public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId);



}
