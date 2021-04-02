package com.heweixing.mapper;

import com.heweixing.pojo.vo.ItemCommentVO;
import com.heweixing.pojo.vo.SearchItemsVO;
import com.heweixing.pojo.vo.ShopCartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustom {

    public List<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemsVO> searchItems(@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemsVO> searchItemsByThirdCat(@Param("paramsMap") Map<String, Object> map);

    public List<ShopCartVO> queryItemsBySpecIds(@Param("paramsList") List<String> specIdsList);

    public Integer decreaseItemSpecStock(@Param("specId")String specId,@Param("pendingCounts") int pendingCounts );

}