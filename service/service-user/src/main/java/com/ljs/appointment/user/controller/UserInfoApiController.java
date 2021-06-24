package com.ljs.appointment.user.controller;

import com.ljs.appointment.model.user.UserInfo;
import com.ljs.appointment.result.Result;
import com.ljs.appointment.user.service.UserInfoService;
import com.ljs.appointment.utils.AuthContextHolder;
import com.ljs.appointment.vo.user.LoginVo;
import com.ljs.appointment.vo.user.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "用户操作")
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;

    //用户手机号登录接口
    @ApiOperation("会员登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo) {
        Map<String, Object> info = userInfoService.loginUser(loginVo);
        return Result.ok(info);
    }

    //用户认证接口
    @PostMapping("/auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
        //在方法中传递两个参数  第一个是用户id  第二个是认证数据vo对象
        userInfoService.userAuth(AuthContextHolder.getUserId(request), userAuthVo);
        return Result.ok();
    }

    //获取用户信息id
    @GetMapping("/auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        return Result.ok(userInfo);
    }
}


