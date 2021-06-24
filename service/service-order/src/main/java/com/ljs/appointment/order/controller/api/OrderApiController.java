package com.ljs.appointment.order.controller.api;

import com.ljs.appointment.order.service.OrderService;
import com.ljs.appointment.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {
    @Autowired
    private OrderService orderService;

    //生成挂号订单
    @PostMapping("/auth/submitOrder/{scheduleId}/{patientId}")
    public Result saveOrder(@PathVariable String scheduleId,
                            @PathVariable String patientId) {
        //返回订单id
        Long orderId = orderService.saveOrder(scheduleId, patientId);
        return Result.ok(orderId);
    }
}
