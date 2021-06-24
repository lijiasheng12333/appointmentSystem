package com.ljs.appointment.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ljs.appointment.model.order.OrderInfo;

public interface OrderService extends IService<OrderInfo> {
    //生成挂号订单
    Long saveOrder(String scheduleId, String patientId);
}
