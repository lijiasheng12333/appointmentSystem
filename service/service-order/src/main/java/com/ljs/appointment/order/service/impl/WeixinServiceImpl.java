package com.ljs.appointment.order.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.ljs.appointment.enums.PaymentStatusEnum;
import com.ljs.appointment.enums.PaymentTypeEnum;
import com.ljs.appointment.model.order.OrderInfo;
import com.ljs.appointment.order.service.OrderService;
import com.ljs.appointment.order.service.PaymentService;
import com.ljs.appointment.order.service.WeixinService;
import com.ljs.appointment.order.utils.ConstantPropertiesUtils;
import com.ljs.appointment.order.utils.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WeixinServiceImpl implements WeixinService {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    //生成微信支付二维码
    @Override
    public Map createNative(Long orderId) {
        //根据orderId获取订单信息
        OrderInfo order = orderService.getById(orderId);
        //向支付记录表添加信息
        paymentService.savePaymentOrder(order, PaymentTypeEnum.WEIXIN.getStatus());
        try {
            //从redis中获取数据
            Map payMap = (Map) redisTemplate.opsForValue().get(orderId.toString());
            if (payMap != null) {
                return payMap;
            }
            //设置参数, 调用微信生成二维码接口
            //把参数转换成xml格式, 使用商户key进行加密
            Map paramMap = new HashMap();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            String body = order.getReserveDate() + "就诊"+ order.getDepname();
            paramMap.put("body", body);
            paramMap.put("out_trade_no", order.getOutTradeNo());
            //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");  //为了测试  同一写这个值
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            paramMap.put("trade_type", "NATIVE");

            //2、HTTPClient来根据URL访问第三方接口并且传递参数
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //client设置参数
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            //返回相关数据
            String xml = client.getContent();
            //xml转换map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            System.out.println("resultMap: " + resultMap);
            //封装返回结果
            Map map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", order.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            map.put("codeUrl", resultMap.get("code_url"));  //二维码地址
            if (resultMap.get("result_code") != null) {
                redisTemplate.opsForValue().set(orderId.toString(), map, 120, TimeUnit.MINUTES);
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //调用微信接口实现支付状态查询
    @Override
    public Map<String, String> queryPayStatus(Long orderId, String name) {
        try {
            //根据orderid 获取订单
            OrderInfo orderInfo = orderService.getById(orderId);
            //封装提交参数
            Map paramMap = new HashMap();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //设置请求内容
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();
            //得到微信接口返回数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            System.out.println("支付状态resultMap: " + resultMap);
            //吧数据返回
            return resultMap;
        } catch (Exception e) {
            return null;
        }
    }
}
