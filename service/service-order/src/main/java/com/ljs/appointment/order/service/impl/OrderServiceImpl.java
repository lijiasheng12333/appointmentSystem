package com.ljs.appointment.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljs.appointment.model.order.OrderInfo;
import com.ljs.appointment.order.mapper.OrderMapper;
import com.ljs.appointment.order.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {

    //生成挂号订单
    @Override
    public Long saveOrder(String scheduleId, String patientId) {
        return null;
    }
}
