package com.heweixing.service;

import com.heweixing.pojo.Stu;
import com.heweixing.pojo.Users;
import com.heweixing.pojo.bo.UserBO;

public interface UserService {
    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 创建用户
     * @param userBO
     * @return
     */
    public Users createUsers(UserBO userBO);

    /**
     * 检索用户名和密码是否存在，用于用户登录
     * @param
     * @return
     */
    public Users queryUserForLogin(String username, String password);


}
