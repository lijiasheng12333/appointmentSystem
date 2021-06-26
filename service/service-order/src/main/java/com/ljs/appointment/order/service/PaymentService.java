package com.ljs.appointment.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljs.appointment.model.order.OrderInfo;
import com.ljs.appointment.model.order.PaymentInfo;

import java.util.Map;

public interface PaymentService extends IService<PaymentInfo> {
    void savePaymentOrder(OrderInfo order, Integer status);

    //更改订单状态，处理支付结果
    void paySuccess(String out_trade_no, Map<String, String> resultMap);
}
