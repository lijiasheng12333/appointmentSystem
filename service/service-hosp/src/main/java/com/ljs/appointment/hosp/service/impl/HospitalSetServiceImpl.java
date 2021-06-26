package com.ljs.appointment.hosp.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljs.appointment.exception.AppointmentException;
import com.ljs.appointment.hosp.mapper.HospitalSetMapper;
import com.ljs.appointment.hosp.service.HospitalSetService;
import com.ljs.appointment.model.hosp.HospitalSet;
import com.ljs.appointment.result.ResultCodeEnum;
import com.ljs.appointment.vo.order.SignInfoVo;
import org.springframework.stereotype.Service;

/**
 * author ljs
 * create 2021-3-10
 */

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet>
        implements HospitalSetService {

        /*@Autowired
    private HospitalSetMapper hospitalSetMapper;*/

    @Override
    public String getSignKey(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();

        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        return hospitalSet.getSignKey();
    }

    @Override
    public SignInfoVo getSignInfoVo(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        if(null == hospitalSet) {
            throw new AppointmentException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        SignInfoVo signInfoVo = new SignInfoVo();
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        signInfoVo.setSignKey(hospitalSet.getSignKey());
        return signInfoVo;

    }

}
