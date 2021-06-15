package com.ljs.appointment.hosp.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljs.appointment.hosp.mapper.HospitalSetMapper;
import com.ljs.appointment.hosp.service.HospitalSetService;
import com.ljs.appointment.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

/**
 * author ljs
 * create 2021-3-10
 */

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet>
        implements HospitalSetService {
    @Override
    public String getSignKey(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();

        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        return hospitalSet.getSignKey();
    }
    /*@Autowired
    private HospitalSetMapper hospitalSetMapper;*/
}
