package com.ljs.appointment.order.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljs.appointment.model.order.OrderInfo;
import com.ljs.appointment.model.user.UserInfo;
import com.ljs.appointment.vo.order.OrderCountQueryVo;
import com.ljs.appointment.vo.order.OrderQueryVo;

import java.util.Map;

public interface OrderService extends IService<OrderInfo> {
    //生成挂号订单
    Long saveOrder(String scheduleId, Long patientId);

    OrderInfo getOrder(String orderId);

    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    boolean cancelOrder(Long orderId);

    //就诊通知
    void patientTips();

    /**
     * 订单统计
     */
    Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo);

}
