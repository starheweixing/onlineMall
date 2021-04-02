package com.heweixing.mapper;

import com.heweixing.mymapper.MyMapper;
import com.heweixing.pojo.Category;
import com.heweixing.pojo.vo.CategoryVO;
import com.heweixing.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface CategoryMapperCustom {

    public List<CategoryVO> getSubCatList(Integer rootCatId);

    public List<NewItemsVO> getSixNewItemsLazy(@Param("paramsMap") Map<String, Object> map);

}