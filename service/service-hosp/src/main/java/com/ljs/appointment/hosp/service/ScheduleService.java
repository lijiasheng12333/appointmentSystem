package com.ljs.appointment.hosp.service;

import com.ljs.appointment.model.hosp.Schedule;
import com.ljs.appointment.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageDepartment(int page, int limit, ScheduleQueryVo queryVo);

    void remove(String hoscode, String hosScheduleId);
}
