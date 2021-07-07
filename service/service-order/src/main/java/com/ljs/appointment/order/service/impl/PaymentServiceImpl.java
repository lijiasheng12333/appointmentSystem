package com.ljs.appointment.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljs.appointment.common.helper.HttpRequestHelper;
import com.ljs.appointment.enums.OrderStatusEnum;
import com.ljs.appointment.enums.PaymentStatusEnum;
import com.ljs.appointment.enums.PaymentTypeEnum;
import com.ljs.appointment.model.order.OrderInfo;
import com.ljs.appointment.model.order.PaymentInfo;
import com.ljs.appointment.order.mapper.PaymentMapper;
import com.ljs.appointment.order.service.OrderService;
import com.ljs.appointment.order.service.PaymentService;
import com.ljs.appointment.vo.order.SignInfoVo;
import com.ljs.appoitment.hosp.client.HospFeignClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl
        extends ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HospFeignClient hospFeignClient;
    //向支付表添加信息
    @Override
    public void savePaymentOrder(OrderInfo order, Integer paymentType) {
        //根据订单id和支付类型  查询支付记录表是否存在相同订单
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", order.getId());
        wrapper.eq("payment_type", paymentType);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            return;
        }
        // 保存交易记录
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(order.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        String subject = new DateTime(order.getReserveDate()).toString("yyyy-MM-dd")+"|"+order.getHosname()+"|"+order.getDepname()+"|"+order.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(order.getAmount());
        baseMapper.insert(paymentInfo);
    }

    //更改订单状态，处理支付结果
    @Override
    public void paySuccess(String out_trade_no, Map<String, String> resultMap) {
        //根据订单编号得到支付记录
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no", out_trade_no);
        wrapper.eq("payment_type", PaymentTypeEnum.WEIXIN.getStatus());
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
        //更新支付记录信息
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackContent(resultMap.toString());
        baseMapper.updateById(paymentInfo);
        //根据订单号得到订单信息
        OrderInfo orderInfo = orderService.getById(paymentInfo.getOrderId());
        //更新订单信息
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderService.updateById(orderInfo);
        //调用医院接口 更新订单支付信息
        SignInfoVo signInfoVo = hospFeignClient.getSignInfoVo(orderInfo.getHoscode());
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode",orderInfo.getHoscode());
        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
        reqMap.put("sign", sign);
        JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl() + "/order/updatePayStatus");
    }

    //获取支付记录
    @Override
    public PaymentInfo getPaymentInfo(Long orderId, Integer paymentType) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        queryWrapper.eq("payment_type", paymentType);
        return baseMapper.selectOne(queryWrapper);
    }
}
