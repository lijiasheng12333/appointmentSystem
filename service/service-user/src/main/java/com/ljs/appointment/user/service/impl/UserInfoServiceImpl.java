package com.ljs.appointment.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljs.appointment.enums.AuthStatusEnum;
import com.ljs.appointment.exception.AppointmentException;
import com.ljs.appointment.helper.JwtHelper;
import com.ljs.appointment.model.user.Patient;
import com.ljs.appointment.model.user.UserInfo;
import com.ljs.appointment.result.ResultCodeEnum;
import com.ljs.appointment.user.mapper.UserInfoMapper;
import com.ljs.appointment.user.service.PatientService;
import com.ljs.appointment.user.service.UserInfoService;
import com.ljs.appointment.vo.user.LoginVo;
import com.ljs.appointment.vo.user.UserAuthVo;
import com.ljs.appointment.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends
        ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PatientService patientService;

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
        //腾讯云短信  判断验证码是否一致
        String redisCode = redisTemplate.opsForValue().get(phone);
        if (!code.equals(redisCode)) {
            throw new AppointmentException(ResultCodeEnum.CODE_ERROR);
        }
        //绑定手机号码
        UserInfo userInfo = null;
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = this.selectWxInfoOpenId(loginVo.getOpenid());
            if(null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);
            } else {
                throw new AppointmentException(ResultCodeEnum.DATA_ERROR);
            }
        }
        //如果为空 就正常手机登录

        if (userInfo == null) {
            //判断是否第一次登录 根据手机号查询数据库  不存在就第一次登录
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phone);
            userInfo = baseMapper.selectOne(queryWrapper);
            if (userInfo == null) {
                //表示是第一次登录
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
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

    @Override
    public UserInfo selectWxInfoOpenId(String openid) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openid);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        return userInfo;
    }

    //用户认证
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据用户id查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        //设置认证信息
        //认证人姓名
        userInfo.setName(userAuthVo.getName());
        //其他信息
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        //进行信息更新
        baseMapper.updateById(userInfo);
    }

    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> userParam, UserInfoQueryVo userInfoQueryVo) {
        //获取条件值
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();  //用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus();  //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();  //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //创建结束时间
        //对条件值非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(name)) {
            wrapper.like("name", name);
        }
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("status", status);
        }
        if (!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status", authStatus);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time", createTimeEnd);
        }
        //调用mapper方法 实现条件查询带分页
        IPage<UserInfo> pages = baseMapper.selectPage(userParam, wrapper);
        //把编号转换为对应的值
        pages.getRecords().stream().forEach(item -> {
            this.packageUserInfo(item);
        });
        return pages;

    }

    //用户锁定
    @Override
    public void lock(Long userId, Integer status) {
        if (status.intValue() == 0 || status.intValue() == 1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> show(Long userId) {
        Map<String,Object> map = new HashMap<>();
        UserInfo userInfo = this.packageUserInfo(baseMapper.selectById(userId));
        map.put("userInfo", userInfo);
        //根据用户id得到就诊人信息
        List<Patient> allByUserId = patientService.findAllByUserId(userId);
        map.put("patientList", allByUserId);
        return map;
    }

    @Override
    public void approval(Long userId, Integer authStatus) {
        if (authStatus.intValue() == 2 || authStatus.intValue() == -1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    //封装
    private UserInfo packageUserInfo(UserInfo userInfo) {
        //处理认证状态的编码
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //处理用户状态
        String statusString = userInfo.getStatus().intValue() == 0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);
        return userInfo;
    }
}
