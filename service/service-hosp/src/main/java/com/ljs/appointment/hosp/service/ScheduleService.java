package com.ljs.appointment.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljs.appointment.model.hosp.Schedule;
import com.ljs.appointment.vo.hosp.ScheduleOrderVo;
import com.ljs.appointment.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService extends IService<Schedule> {
    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageDepartment(int page, int limit, ScheduleQueryVo queryVo);

    void remove(String hoscode, String hosScheduleId);

    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    //获得可预约排班数据
    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    //根据id获取排版数据
    Schedule getScheduleById(String scheduleId);
    //根据排班id获取预约下单数据
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    //更新排班数据 用于mq操作
    void update(Schedule schedule);
}
