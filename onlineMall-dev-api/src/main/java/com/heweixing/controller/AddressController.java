package com.heweixing.controller;

import com.heweixing.pojo.UserAddress;
import com.heweixing.pojo.bo.AddressBO;
import com.heweixing.service.AddressService;
import com.heweixing.utils.IMOOCJSONResult;
import com.heweixing.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "地址相关接口", tags = "地址的相关接口")
@RestController
@RequestMapping("address")
public class AddressController {

    /**
     * 用户在确认订单页面可以，可以针对收货地址做一下操作
     * 1.查询用户的所有收货地址列表
     * 2.新增收货地址
     * 3.删除收货地址
     * 4.修改收货地址
     * 5.设置默认收货地址
     */

    @Autowired
    private AddressService addressService;


    @ApiOperation(value = "查询用户所有地址", notes = "查询用户所有地址", httpMethod = "POST")
    @PostMapping("/list")
    public IMOOCJSONResult list(@RequestParam String userId) {

        if(StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("");
        }

        List<UserAddress> list = addressService.queryAll(userId);
        return IMOOCJSONResult.ok(list);
    }


    @ApiOperation(value = "用户新增地址", notes = "用户新增地址", httpMethod = "POST")
    @PostMapping("/add")
    public IMOOCJSONResult add(@RequestBody AddressBO addressBO){

        IMOOCJSONResult result = checkAddress(addressBO);
        if(result.getStatus() != 200){
            return result;
        }

        addressService.addNewUserAddress(addressBO);
        return IMOOCJSONResult.ok();

    }

    private IMOOCJSONResult checkAddress(AddressBO addressBO) {
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return IMOOCJSONResult.errorMsg("收货人不能为空");
        }
        if (receiver.length() > 12) {
            return IMOOCJSONResult.errorMsg("收货人姓名不能太长");
        }

        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return IMOOCJSONResult.errorMsg("收货人手机号不能为空");
        }
        if (mobile.length() != 11) {
            return IMOOCJSONResult.errorMsg("收货人手机号长度不正确");
        }
        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk) {
            return IMOOCJSONResult.errorMsg("收货人手机号格式不正确");
        }

        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province) ||
                StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) ||
                StringUtils.isBlank(detail)) {
            return IMOOCJSONResult.errorMsg("收货地址信息不能为空");
        }

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户修改地址", notes = "用户修改地址", httpMethod = "POST")
    @PostMapping("/update")
    public IMOOCJSONResult update(@RequestBody AddressBO addressBO){

        if(StringUtils.isBlank(addressBO.getAddressId())){
            return IMOOCJSONResult.errorMsg("修改地址错误: addressId不能为空");
        }

        IMOOCJSONResult CheckRes = checkAddress(addressBO);
        if(CheckRes.getStatus() != 200){
            return CheckRes;
        }
        addressService.updateUserAddress(addressBO);
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户删除地址", notes = "用户删除地址", httpMethod = "POST")
    @PostMapping("/delete")
    public IMOOCJSONResult delete(@RequestParam String userId, @RequestParam String addressId){

        if(StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("");
        }

        if(StringUtils.isBlank(addressId)){
            return IMOOCJSONResult.errorMsg("");
        }
        addressService.deleteUserAddress(userId, addressId);
        return IMOOCJSONResult.ok();
    }



    @ApiOperation(value = "用户设置默认地址", notes = "用户设置默认地址", httpMethod = "POST")
    @PostMapping("/setDefalut")
    public IMOOCJSONResult setDefault(@RequestParam String userId, @RequestParam String addressId){

        if(StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("");
        }

        if(StringUtils.isBlank(addressId)){
            return IMOOCJSONResult.errorMsg("");
        }
        addressService.updateUserAddressToBeDefault(userId, addressId);
        return IMOOCJSONResult.ok();
    }

}
