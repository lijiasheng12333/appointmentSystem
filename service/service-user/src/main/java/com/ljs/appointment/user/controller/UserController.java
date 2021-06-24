package com.ljs.appointment.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljs.appointment.model.user.UserInfo;
import com.ljs.appointment.result.Result;
import com.ljs.appointment.user.service.UserInfoService;
import com.ljs.appointment.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Autowired
    private UserInfoService userInfoService;

    //用户列表（条件查询带分页）
    @GetMapping("/{page}/{limit}")
    public Result list(@PathVariable("page") Long page,
                       @PathVariable("limit") Long limit,
                       UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> userParam = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.selectPage(userParam, userInfoQueryVo);
        return Result.ok(pageModel);
    }

    //用户锁定
    @GetMapping("/lock/{userId}/{status}")
    public Result lock(@PathVariable Long userId,
                       @PathVariable Integer status) {
        userInfoService.lock(userId, status);
        return Result.ok();
    }

    //用户详情
    @GetMapping("/show/{userId}")
    public Result show(@PathVariable("userId") Long userId) {
        Map<String, Object> map = userInfoService.show(userId);
        return Result.ok(map);
    }

    //认证审批
    @GetMapping("/approval/{userId}/{authStatus}")
    public Result approval(@PathVariable Long userId,@PathVariable Integer authStatus) {
        userInfoService.approval(userId, authStatus);
        return Result.ok();
    }




}
