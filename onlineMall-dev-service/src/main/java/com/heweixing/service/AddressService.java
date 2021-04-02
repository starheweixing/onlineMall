package com.heweixing.service;
import com.heweixing.pojo.UserAddress;
import com.heweixing.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {
    /**
     * 根据用户id查询用户地址列表
     * @param userId
     * @return
     */
    public List<UserAddress> queryAll(String userId);

    /**
     * 用户新增地址
     * @param addressBO
     */
    public void addNewUserAddress(AddressBO addressBO);

    /**
     * 用户修改地址
     * @param addressBO
     */
    public void updateUserAddress(AddressBO addressBO);

    /**
     * 根据用户id和地址id删除对应的用户地址信息
     * @param userId
     * @param addressId
     */
    public void deleteUserAddress(String userId, String addressId);

    /**
     * 设置默认收货地址
     * @param addressId
     */
    public void updateUserAddressToBeDefault(String userId, String addressId);

    /**
     * 根据用户id和地址id查询地址信息
     * @param userId
     * @param addressId
     * @return
     */
    public UserAddress queryUserAddress(String userId, String addressId);


}
