package com.ljs.appointment.msm.service.impl;

import com.ljs.appointment.msm.service.MsmService;
import com.ljs.appointment.msm.utils.ConstantPropertiesUtils;
import com.ljs.appointment.vo.msm.MsmVo;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20190711.models.SendStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
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



        //Todo  目前是写死成功状态  需要仔细查看文档变更为获取发送成功与否状态
        return true;
    }

    //mq发送短信封装
    @Override
    public boolean send(MsmVo msmVo) {
        //判断手机号是否为空
        if (!StringUtils.isEmpty(msmVo.getPhone())) {
            Map<String, Object> param = msmVo.getParam();
            log.info("短信准备发送");
            return this.send(msmVo.getPhone(), param);
        }
        return false;
    }


    private boolean send(String phone, Map map) {
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
        req.setTemplateID("1012190");  //模板id
        String[] param1 = {"86" + phone};
        req.setPhoneNumberSet(param1);
        //
        /*String[] param2 = {code};*/
        //String[] param = new String[5];
        String[] param = {};
        /*param[0] = (String) map.get("title");
        param[1] = map.get("amount").toString();
        param[2] = map.get("reserveDate").toString();
        param[3] = (String) map.get("name");
        param[4] = (String) map.get("quitTime");*/
        req.setTemplateParamSet(param);
        try {
            SendSmsResponse res = client.SendSms(req);
            System.out.println(SendSmsResponse.toJsonString(res));
            System.out.println(res.getRequestId());
            System.out.println(res.getSendStatusSet());
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }



        //Todo  目前是写死成功状态  需要仔细查看文档变更为获取发送成功与否状态
        return true;
    }
}
