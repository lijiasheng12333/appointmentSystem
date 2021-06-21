package com.ljs.appointment.msm.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantPropertiesUtils implements InitializingBean {

    @Value("${tencentcloud.sms.appkey}")
    private String appkey;

    @Value("${tencentcloud.sms.appId}")
    private String appId;

    /*@Value("${tencentcloud.sms.smsSign}")
    private String smsSign;*/

    @Value("${tencentcloud.sms.templateid}")
    private String templateid;

    @Value("${tencentcloud.sms.secretId}")
    private String secretid;

    @Value("${tencentcloud.sms.secretkey}")
    private String secretkey;

/*    @Value("${aliyun.sms.regionId}")
    private String regionId;

    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.secret}")
    private String secret;*/


    public static String APPLE_KEY;
    public static String APPLE_ID;
    public static String SMS_SIGN;
    public static String TEMPLATE_ID;
    public static String SECRET_ID;
    public static String SECRET_KEY;

   /* public static String REGION_Id;
    public static String ACCESS_KEY_ID;
    public static String SECRECT;*/


    @Override
    public void afterPropertiesSet() throws Exception {

        APPLE_ID = appId;
        APPLE_KEY = appkey;
        SMS_SIGN = "小葱大葱蒜苗叶";
        TEMPLATE_ID = templateid;
        SECRET_ID = secretid;
        SECRET_KEY = secretkey;

      /*  REGION_Id=regionId;
        ACCESS_KEY_ID=accessKeyId;
        SECRECT=secret;*/

    }
}
