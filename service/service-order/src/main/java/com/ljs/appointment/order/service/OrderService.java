package com.ljs.appointment.order.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljs.appointment.model.order.OrderInfo;
import com.ljs.appointment.model.user.UserInfo;
import com.ljs.appointment.vo.order.OrderQueryVo;

public interface OrderService extends IService<OrderInfo> {
    //生成挂号订单
    Long saveOrder(String scheduleId, Long patientId);

    OrderInfo getOrder(String orderId);

    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);
}
