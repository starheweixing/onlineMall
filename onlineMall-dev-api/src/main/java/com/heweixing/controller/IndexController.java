package com.heweixing.controller;

import com.heweixing.enums.YesOrNo;
import com.heweixing.pojo.Carousel;
import com.heweixing.pojo.Category;
import com.heweixing.pojo.vo.CategoryVO;
import com.heweixing.pojo.vo.NewItemsVO;
import com.heweixing.service.CarouselService;
import com.heweixing.service.CategoryService;
import com.heweixing.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Api(value = "首页", tags = "首页展示的相关接口")
@RestController  //返回的都是json对象
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;
    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public IMOOCJSONResult carousel() {
        List<Carousel> result = carouselService.queryAll(YesOrNo.yes.type);
        return IMOOCJSONResult.ok(result);
    }


    /**
     * 首页分类占上市需求
     * 1.第一次刷新主页查询大分类，渲染展示到首页
     * 2.如果鼠标移动到大分类上，则加载其子分类的内容，如果已经存在子分类，则不需要加载(懒加载)
     */
    @ApiOperation(value = "获取商品分类（一级分类）", notes = "获取商品分类（一级分类）", httpMethod = "GET")
    @GetMapping("/cats")
    public IMOOCJSONResult cats() {
        List<Category> result = categoryService.queryAllRootLevelCat();
        return IMOOCJSONResult.ok(result);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public IMOOCJSONResult subCat(
                            @ApiParam(name = "rootCatId",value = "一级分类子Id",required = true)
                            @PathVariable Integer rootCatId) {

        if(rootCatId == null){
            return IMOOCJSONResult.errorMsg("分类不存在");
        }

        List<CategoryVO> result = categoryService.getSubCatList(rootCatId);
        return IMOOCJSONResult.ok(result);
    }


    @ApiOperation(value = "查询每个一级分类下的最新6条商品", notes = "查询每个一级分类下的最新6条商品", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public IMOOCJSONResult sixNewItems(
            @ApiParam(name = "rootCatId",value = "一级分类子Id",required = true)
            @PathVariable Integer rootCatId) {

        if(rootCatId == null){
            return IMOOCJSONResult.errorMsg("分类不存在");
        }

        List<NewItemsVO> result = categoryService.getSixNewItemsLazy(rootCatId);
        return IMOOCJSONResult.ok(result);
    }


}
