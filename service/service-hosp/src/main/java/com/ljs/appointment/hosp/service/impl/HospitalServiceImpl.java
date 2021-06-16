package com.ljs.appointment.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ljs.appointment.hosp.reponsitory.HospitalReponsitory;
import com.ljs.appointment.hosp.service.HospitalService;
import com.ljs.appointment.model.hosp.Hospital;
import com.ljs.appointment.model.hosp.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalReponsitory hospitalReponsitory;

    /**
     * 状态码(0表示未上线,1表示上线) 创建时间 修改时间 是否删除(0代表未删除,1代表已删除)这四个信息不属于上传者需要填写的信息，因此需要在这里进行预设置。
     * @param paramMap 上传的医院基本信息
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        Hospital hospitalExist = hospitalReponsitory.getHospitalByHoscode(hospital.getHoscode());
        if (hospitalExist != null) {
            hospital.setStatus(hospital.getStatus());
            hospital.setCreateTime(hospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalReponsitory.save(hospital);
        }else {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalReponsitory.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospitalByHoscode = hospitalReponsitory.getHospitalByHoscode(hoscode);
        return hospitalByHoscode;
    }

}
