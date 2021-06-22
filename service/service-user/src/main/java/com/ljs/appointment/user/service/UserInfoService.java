package com.ljs.appointment.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljs.appointment.model.user.UserInfo;
import com.ljs.appointment.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {

    //登录
    Map<String, Object> loginUser(LoginVo loginVo);

    UserInfo selectWxInfoOpenId(String openid);
}
