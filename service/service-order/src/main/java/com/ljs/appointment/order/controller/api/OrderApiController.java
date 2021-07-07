package com.ljs.appointment.order.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljs.appointment.enums.OrderStatusEnum;
import com.ljs.appointment.model.order.OrderInfo;
import com.ljs.appointment.model.user.UserInfo;
import com.ljs.appointment.order.service.OrderService;
import com.ljs.appointment.result.Result;
import com.ljs.appointment.utils.AuthContextHolder;
import com.ljs.appointment.vo.order.OrderCountQueryVo;
import com.ljs.appointment.vo.order.OrderQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {
    @Autowired
    private OrderService orderService;

    //生成挂号订单
    @PostMapping("/auth/submitOrder/{scheduleId}/{patientId}")
    public Result saveOrder(@PathVariable String scheduleId,
                            @PathVariable Long patientId) {
        //返回订单id
        Long orderId = orderService.saveOrder(scheduleId, patientId);
        return Result.ok(orderId);
    }

    //根据订单id查询订单详情
    @GetMapping("/auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable String orderId) {
        OrderInfo orderInfo = orderService.getOrder(orderId);
        return Result.ok(orderInfo);
    }

    //订单列表（条件查询带分页）
    @GetMapping("/auth/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       OrderQueryVo orderQueryVo, HttpServletRequest request) {
        //设置当前用户id
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel =
                orderService.selectPage(pageParam,orderQueryVo);
        return Result.ok(pageModel);

    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("/auth/getStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    @ApiOperation(value = "取消预约")
    @GetMapping("/auth/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable("orderId") Long orderId) {
        return Result.ok(orderService.cancelOrder(orderId));
    }

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return orderService.getCountMap(orderCountQueryVo);
    }


}
