package com.ljs.appointment.msm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.ljs.appointment.msm.service.MsmService;
import com.ljs.appointment.msm.utils.ConstantPropertiesUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20190711.models.SendStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {

    @Override
    public boolean send(String phone, String code) {
        //判断手机号是否为空
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        //整合腾讯云短信服务
        //设置相关参数
        Credential cred = new Credential(ConstantPropertiesUtils.SECRET_ID,
                ConstantPropertiesUtils.SECRET_KEY);

        SmsClient client = new SmsClient(cred, "");

        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setReqMethod("POST");
        httpProfile.setConnTimeout(120);

        SendSmsRequest req = new SendSmsRequest();
        req.setSmsSdkAppid(ConstantPropertiesUtils.APPLE_ID);  //sdk appleid
        String senderid = "";
        req.setSenderId(senderid);
        req.setSign(ConstantPropertiesUtils.SMS_SIGN);  //签名
        req.setTemplateID(ConstantPropertiesUtils.TEMPLATE_ID);  //模板id
        String[] param1 = {"86" + phone};
        req.setPhoneNumberSet(param1);
        //
        String[] param2 = {code};
        req.setTemplateParamSet(param2);
        try {
            SendSmsResponse res = client.SendSms(req);
            System.out.println(SendSmsResponse.toJsonString(res));
            System.out.println(res.getRequestId());
            System.out.println(res.getSendStatusSet());
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }


        //整合阿里云短信服务
        //设置相关参数
        /*DefaultProfile profile = DefaultProfile.
                getProfile(ConstantPropertiesUtils.REGION_Id,
                        ConstantPropertiesUtils.ACCESS_KEY_ID,
                        ConstantPropertiesUtils.SECRECT);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        //手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //签名名称
        request.putQueryParameter("SignName", "我的谷粒在线教育网站");
        //模板code
        request.putQueryParameter("TemplateCode", "SMS_180051135");
        //验证码  使用json格式   {"code":"123456"}
        Map<String,Object> param = new HashMap();
        param.put("code",code);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));

        //调用方法进行短信发送
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }*/
        //Todo  目前是写死成功状态  需要仔细查看文档变更为获取发送成功与否状态
        return true;
    }
}
