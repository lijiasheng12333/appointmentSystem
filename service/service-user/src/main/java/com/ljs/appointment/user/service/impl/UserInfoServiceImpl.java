package com.ljs.appointment.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljs.appointment.exception.AppointmentException;
import com.ljs.appointment.helper.JwtHelper;
import com.ljs.appointment.model.user.UserInfo;
import com.ljs.appointment.result.ResultCodeEnum;
import com.ljs.appointment.user.mapper.UserInfoMapper;
import com.ljs.appointment.user.service.UserInfoService;
import com.ljs.appointment.vo.user.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends
        ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    //登录
    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        //先获取手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //校验是否为空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new AppointmentException(ResultCodeEnum.PARAM_ERROR);
        }
        //阿里云短信  判断验证码是否一致
        String redisCode = redisTemplate.opsForValue().get(phone);
        if (!code.equals(redisCode)) {
            throw new AppointmentException(ResultCodeEnum.CODE_ERROR);
        }
        //判断是否第一次登录 根据手机号查询数据库  不存在就第一次登录
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        if (userInfo == null) {
            //表示是第一次登录
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
        }
        //校验是否禁用
        if (userInfo.getStatus() == 0) {
            throw new AppointmentException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }
        //不是第一次  直接登录
        //返回登录信息  用户名
        //token信息
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //使用jwt生成token
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token",token);
        return map;
    }
}
