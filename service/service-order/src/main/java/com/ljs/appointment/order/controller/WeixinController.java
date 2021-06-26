package com.ljs.appointment.order.controller;

import com.ljs.appointment.enums.PaymentTypeEnum;
import com.ljs.appointment.order.service.PaymentService;
import com.ljs.appointment.order.service.WeixinService;
import com.ljs.appointment.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/order/weixin")
public class WeixinController {

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private PaymentService paymentService;

    //生成微信支付二维码
    @GetMapping("/createNative/{orderId}")
    public Result createNative(@PathVariable("orderId") Long orderId) {
        System.out.println("生成二维码中");
        Map map = weixinService.createNative(orderId);
        return Result.ok(map);
    }

    //查询支付状态
    @GetMapping("/queryPayStatus/{orderId}")
    public Result queryPayStatus(@PathVariable("orderId") Long orderId) {
        //调用微信接口实现支付状态查询
        Map<String, String> resultMap = weixinService.queryPayStatus(orderId, PaymentTypeEnum.WEIXIN.name());
        if (resultMap == null) {//出错
            return Result.fail().message("支付出错");
        }
        if ("SUCCESS".equals(resultMap.get("trade_state"))) {//如果成功
            //更改订单状态，处理支付结果
            String out_trade_no = resultMap.get("out_trade_no");
            paymentService.paySuccess(out_trade_no, resultMap);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }

}
