package com.ljs.appointment.user.controller;


import com.alibaba.fastjson.JSONObject;
import com.ljs.appointment.exception.AppointmentException;
import com.ljs.appointment.helper.JwtHelper;
import com.ljs.appointment.model.user.UserInfo;
import com.ljs.appointment.result.Result;
import com.ljs.appointment.result.ResultCodeEnum;
import com.ljs.appointment.user.service.UserInfoService;
import com.ljs.appointment.user.utils.ConstantWxPropertiesUtils;
import com.ljs.appointment.user.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller  //进行跳转
@RequestMapping("/api/ucenter/wx")
@Slf4j
public class WeixinApiController {

    @Autowired
    private UserInfoService userInfoService;

    //回调  得到扫码人信息
    @GetMapping("/callback")
    public String callback(String code, String state) {
        //获取授权临时票据
        System.out.println("微信授权服务器回调。。。。。。");
        if (StringUtils.isEmpty(state) || StringUtils.isEmpty(code)) {
            log.error("非法回调请求");
            throw new AppointmentException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        //拿code取货去id和密钥  请求微信固定地址
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET,
                code);
        //使用httpclient请求地址
        try {
            String accesstokenInfo = HttpClientUtils.get(accessTokenUrl);
            JSONObject jsonObject = JSONObject.parseObject(accesstokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");
            //判断是否已经存在微信扫码人信息  根据openid
            UserInfo userInfo = userInfoService.selectWxInfoOpenId(openid);
            //空及不存在
            if (null == userInfo) {
                //拿openid和 token请求地址  得到扫码人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);

                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultInfo: " + resultInfo);

                JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
                //解析用户信息
                //昵称+头像
                String nickname = resultUserInfoJson.getString("nickname");
                String headimgurl = resultUserInfoJson.getString("headimgurl");

                //扫描人信息添加进数据库
                userInfo = new UserInfo();
                userInfo.setNickName(nickname);
                userInfo.setOpenid(openid);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }
            //返回name和token字符串
            Map<String, Object> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);

            //判断userInfo中是否有手机号 ,如果手机号为空  返回openid
            //如果不为空  返回openid为空字符串
            //最后前端判断 如果openid为空 则不绑定手机号  如果不为空 则绑定手机号
            if(StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }
            System.out.println("openid:" + map.get("openid"));
            //使用jwt生成token字符串
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            //跳转前端页面
            return "redirect:"+ ConstantWxPropertiesUtils.YYGH_BASE_URL + "/weixin/callback?token="
                    +map.get("token")+"&openid="+map.get("openid")+"&name="+URLEncoder.encode((String) map.get("name"), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //生成微信扫描二维码
    //返回生成二维码重要参数
    @GetMapping("/getLoginParam")
    @ResponseBody
    public Result genQrConnect() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
            map.put("scope", "snsapi_login");
            String url = URLEncoder.encode(ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL,"UTF-8");
            map.put("redirect_uri", url);
            map.put("state", System.currentTimeMillis() + "");
            return Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
