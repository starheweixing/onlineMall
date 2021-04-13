package com.heweixing.service.impl;

import com.heweixing.enums.Sex;
import com.heweixing.mapper.UsersMapper;
import com.heweixing.pojo.Users;
import com.heweixing.pojo.bo.UserBO;
import com.heweixing.service.UserService;
import com.heweixing.utils.DateUtil;
import com.heweixing.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UsersMapper usersMapper;
    @Autowired
    Sid sid;

    private static final String USER_FACE = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {

        Example userExample = new Example(Users.class);
        Example.Criteria useCriteria = userExample.createCriteria();
        useCriteria.andEqualTo("username", username);
        Users user = usersMapper.selectOneByExample(userExample);
        return user != null;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUsers(UserBO userBO) {
        String userId = sid.nextShort();
        Users newUsers = new Users();
        newUsers.setId(userId);
        newUsers.setUsername(userBO.getUsername());
        try {
            newUsers.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //默认用户昵称同用户名
        newUsers.setNickname(userBO.getUsername());
        //设置默认的头像
        newUsers.setFace(USER_FACE);
        //设置默认的生日
        newUsers.setBirthday(DateUtil.stringToDate("1970-01-01"));
        //性别默认为保密
        newUsers.setSex(Sex.secret.type);
        //创建日期
        newUsers.setCreatedTime(new Date());
        newUsers.setUpdatedTime(new Date());

        usersMapper.insert(newUsers);

        return newUsers;

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();

        userCriteria.andEqualTo("username", username);
        userCriteria.andEqualTo("password", password);

        Users result = usersMapper.selectOneByExample(userExample);
        return result;
    }
}
