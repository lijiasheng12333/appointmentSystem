package com.ljs.appointment.msm.controller;

import com.ljs.appointment.msm.service.MsmService;
import com.ljs.appointment.msm.utils.RandomUtil;
import com.ljs.appointment.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msm")
public class MsmApiController {
    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/send/{phone}")
    public Result sendCode(@PathVariable("phone") String phone) {
        //从redis获取验证码  如果获取了  返回ok
        //redis key  手机号  value 验证码
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)) {
            return Result.ok();
        }
        //如果获取不到  生成验证码
        code = RandomUtil.getSixBitRandom();
        //整合短信发送  调用service方法
        boolean isSend = msmService.send(phone, code);
        //生成的验证码放到redis里面 并设置有效时间
        if (isSend) {
            //2分钟有效
            redisTemplate.opsForValue().set(phone, code, 2, TimeUnit.MINUTES);
            return Result.ok();
        }else {
            return Result.fail().message("发送短信失败");
        }
    }
}
