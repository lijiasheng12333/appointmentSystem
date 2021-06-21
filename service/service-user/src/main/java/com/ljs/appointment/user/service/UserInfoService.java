package com.ljs.appointment.user.service;

import com.ljs.appointment.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService {

    //登录
    Map<String, Object> loginUser(LoginVo loginVo);
}
