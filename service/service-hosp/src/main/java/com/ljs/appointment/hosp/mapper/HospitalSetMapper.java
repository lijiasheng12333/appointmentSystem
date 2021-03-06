package com.ljs.appointment.hosp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljs.appointment.hosp.service.HospitalSetService;
import com.ljs.appointment.model.hosp.HospitalSet;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface HospitalSetMapper extends BaseMapper<HospitalSet> {

}
