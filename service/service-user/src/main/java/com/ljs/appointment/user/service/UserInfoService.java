package com.ljs.appointment.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljs.appointment.model.user.UserInfo;
import com.ljs.appointment.vo.user.LoginVo;
import com.ljs.appointment.vo.user.UserAuthVo;
import com.ljs.appointment.vo.user.UserInfoQueryVo;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {

    //登录
    Map<String, Object> loginUser(LoginVo loginVo);

    UserInfo selectWxInfoOpenId(String openid);

    //用户认证
    void userAuth(Long userId, UserAuthVo userAuthVo);

    IPage<UserInfo> selectPage(Page<UserInfo> userParam, UserInfoQueryVo userInfoQueryVo);

    void lock(Long userId, Integer status);

    Map<String, Object> show(Long userId);

    void approval(Long userId, Integer authStatus);
}
