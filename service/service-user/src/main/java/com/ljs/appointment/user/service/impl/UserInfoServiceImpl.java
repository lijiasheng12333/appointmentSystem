package com.ljs.appointment.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljs.appointment.model.user.UserInfo;
import com.ljs.appointment.user.mapper.UserInfoMapper;
import com.ljs.appointment.user.service.UserInfoService;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends
        ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

}
