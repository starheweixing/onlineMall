package com.heweixing.service.impl;

import com.heweixing.enums.YesOrNo;
import com.heweixing.mapper.UserAddressMapper;
import com.heweixing.mapper.UsersMapper;
import com.heweixing.pojo.ItemsSpec;
import com.heweixing.pojo.UserAddress;
import com.heweixing.pojo.bo.AddressBO;
import com.heweixing.service.AddressService;
import com.heweixing.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAll(String userId) {

//        UserAddress ua = new UserAddress();
//        ua.setId(userId);
//
        Example example = new Example(UserAddress.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        return userAddressMapper.selectByExample(example);
//        return userAddressMapper.select(ua);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addNewUserAddress(AddressBO addressBO) {

        Integer isDefault = 0;
        //1.判断当前用户是否存在地址，如果没有则新增为默认地址.
        List<UserAddress> addressList = this.queryAll(addressBO.getUserId());
        if (addressList == null || addressList.isEmpty() || addressList.size() <= 0) {
            isDefault = 1;
        }

        String addressId = sid.nextShort();
        //2.保存地址到数据库
        UserAddress newAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, newAddress);
        newAddress.setId(addressId);
        newAddress.setIsDefault(isDefault);
        newAddress.setCreatedTime(new Date());
        newAddress.setUpdatedTime(new Date());
        userAddressMapper.insert(newAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddress(AddressBO addressBO) {

        String addressId = addressBO.getAddressId();
        UserAddress pendingAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, pendingAddress);
        pendingAddress.setId(addressId);
        pendingAddress.setUpdatedTime(new Date());
        userAddressMapper.updateByPrimaryKeySelective(pendingAddress);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserAddress(String userId, String addressId) {

        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setId(addressId);
        userAddressMapper.delete(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddressToBeDefault(String userId, String addressId) {
        //1.查找默认地址，设置为不默认.
        UserAddress queryAddress = new UserAddress();
        queryAddress.setUserId(userId);
        queryAddress.setIsDefault(YesOrNo.yes.type);
        List<UserAddress> addressList = userAddressMapper.select(queryAddress);
        for(UserAddress ua : addressList){
            ua.setIsDefault(YesOrNo.no.type);
            userAddressMapper.updateByPrimaryKeySelective(ua);
        }

        //2.根据地址id，设置为默认地址.
        UserAddress defaultAddress = new UserAddress();
        defaultAddress.setIsDefault(YesOrNo.yes.type);
        defaultAddress.setUserId(userId);
        defaultAddress.setId(addressId);
        userAddressMapper.updateByPrimaryKeySelective(defaultAddress);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserAddress queryUserAddress(String userId, String addressId) {
        UserAddress address = new UserAddress();
        address.setId(addressId);
        address.setUserId(userId);
        return userAddressMapper.selectOne(address);

    }
}
