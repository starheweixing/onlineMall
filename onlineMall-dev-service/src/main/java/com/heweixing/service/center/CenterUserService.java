package com.heweixing.service.center;

import com.heweixing.pojo.Users;
import com.heweixing.pojo.bo.center.CenterUserBO;

public interface CenterUserService {
    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);


    /**
     *修改用户信息
     */
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO);

    /**
     * 用户头像更新
     * @param userId
     * @param faceUrl
     * @return
     */
    public Users updateUserFace(String userId, String faceUrl);


}
