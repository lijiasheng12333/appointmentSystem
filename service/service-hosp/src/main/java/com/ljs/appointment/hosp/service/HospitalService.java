package com.ljs.appointment.hosp.service;


import com.ljs.appointment.model.hosp.Hospital;
import com.ljs.appointment.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Map<String, Object> getHospById(String id);

    String getHospName(String hoscode);
    //根据医院名称查询
    List<Hospital> findByHosname(String hosname);
}
