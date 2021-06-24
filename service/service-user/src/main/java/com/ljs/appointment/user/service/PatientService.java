package com.ljs.appointment.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljs.appointment.model.user.Patient;

import java.util.List;

public interface PatientService extends IService<Patient> {
    //获取就诊人列表
    List<Patient> findAllByUserId(Long userId);

    Patient getPatientById(Long id);
}
