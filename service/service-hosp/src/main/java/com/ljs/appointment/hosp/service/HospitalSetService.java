package com.ljs.appointment.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljs.appointment.model.hosp.HospitalSet;

public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKey(String hoscode);
}
