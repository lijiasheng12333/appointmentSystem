package com.ljs.appointment.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljs.appointment.model.hosp.HospitalSet;
import com.ljs.appointment.vo.order.SignInfoVo;

public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKey(String hoscode);
    //获取医院签名信息
    SignInfoVo getSignInfoVo(String hoscode);
}
