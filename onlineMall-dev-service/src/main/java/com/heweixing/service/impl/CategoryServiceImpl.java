package com.heweixing.service.impl;

import com.heweixing.mapper.CarouselMapper;
import com.heweixing.mapper.CategoryMapper;
import com.heweixing.mapper.CategoryMapperCustom;
import com.heweixing.pojo.Carousel;
import com.heweixing.pojo.Category;
import com.heweixing.pojo.vo.CategoryVO;
import com.heweixing.pojo.vo.NewItemsVO;
import com.heweixing.service.CarouselService;
import com.heweixing.service.CategoryService;
import com.heweixing.utils.IMOOCJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryMapperCustom categoryMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> queryAllRootLevelCat() {

        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", 1);
        List<Category> result = categoryMapper.selectByExample(example);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVO> getSubCatList(Integer rootCatId) {
        List<CategoryVO> result = categoryMapperCustom.getSubCatList(rootCatId);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId) {

        Map<String, Object> map = new HashMap<>();
        map.put("rootCatId",rootCatId);
        return categoryMapperCustom.getSixNewItemsLazy(map);
    }


}
