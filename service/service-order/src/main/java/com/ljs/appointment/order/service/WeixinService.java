package com.ljs.appointment.order.service;


import java.util.Map;

public interface WeixinService{
    Map createNative(Long orderId);

    Map<String, String> queryPayStatus(Long orderId, String name);

    /***
     * 退款
     * @param orderId
     * @return
     */
    Boolean refund(Long orderId);

}
