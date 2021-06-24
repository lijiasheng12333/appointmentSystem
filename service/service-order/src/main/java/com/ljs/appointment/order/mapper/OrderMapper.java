package com.ljs.appointment.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljs.appointment.model.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {
}
