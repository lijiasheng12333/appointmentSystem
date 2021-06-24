package com.ljs.appointment.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljs.appointment.model.user.Patient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
