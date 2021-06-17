package com.ljs.appointment.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljs.appointment.hosp.reponsitory.ScheduleReponsitory;
import com.ljs.appointment.hosp.service.DepartmentService;
import com.ljs.appointment.hosp.service.HospitalService;
import com.ljs.appointment.hosp.service.ScheduleService;
import com.ljs.appointment.model.hosp.Schedule;
import com.ljs.appointment.vo.hosp.BookingScheduleRuleVo;
import com.ljs.appointment.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleReponsitory scheduleReponsitory;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public void save(Map<String, Object> paramMap) {
        String paramMapString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(paramMapString, Schedule.class);

        Schedule scheduleExist = scheduleReponsitory
                .getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
        if (scheduleExist != null) {
            schedule.setUpdateTime(new Date());
            schedule.setCreateTime(scheduleExist.getCreateTime());
            schedule.setIsDeleted(0);
            schedule.setStatus(scheduleExist.getStatus());
            scheduleReponsitory.save(schedule);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setStatus(1);
            schedule.setIsDeleted(0);
            scheduleReponsitory.save(schedule);
        }
    }

    @Override
    public Page<Schedule> findPageDepartment(int page, int limit, ScheduleQueryVo queryVo) {
        //创建pageable对象，设置当前页和记录数
        //0为第一页
        Pageable pageable = PageRequest.of(page - 1, limit);
        //创建example对象
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(queryVo, schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> all = scheduleReponsitory.findAll(example, pageable);
        return all;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleReponsitory.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            scheduleReponsitory.deleteById(schedule.getId());
        }
    }
    //根据医院编号和科室编号 查询排版规则数据
    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        //1 根据医院编号和科室编号查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        //2根据工作日workdate期进行分组
        Aggregation agg = Aggregation.newAggregation(
              Aggregation.match(criteria), //匹配条件
              Aggregation.group("workDate") //分组字段
                .first("workDate").as("workDate") //起别名
                //3 统计号源数量
                .count().as("docCount")
                .sum("reservedNumber").as("reservedNumber")  //已预约
                .sum("availableNumber").as("availableNumber"),  //剩余预约数
                //排序
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                //4 实现分页
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );
        //调用方法  最终查询
        AggregationResults<BookingScheduleRuleVo> aggResults = mongoTemplate
                                .aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVolist = aggResults.getMappedResults();

        //分组查询的总记录数
        Aggregation totalagg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults = mongoTemplate
                                .aggregate(totalagg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();
        //把日期对应星期几获取
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVolist) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }
        //设置最终数据 返回
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookingScheduleRuleVolist);
        result.put("total", total);
        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hosName);
        result.put("baseMap",baseMap);
        return result;
    }

    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        //1 根据参数查询
        List<Schedule> scheduleList = scheduleReponsitory
                .findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        //2 得到的集合遍历  向其设置其他值  医院名称 科室名称  日期对应星期
        scheduleList.stream().forEach(item -> {
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    //封装排班详情
    private void packageSchedule(Schedule schedule) {
        //设置医院名
        schedule.getParam().put("hosname", hospitalService.getHospName(schedule.getHoscode()));
        //设置科室名
        schedule.getParam().put("depname", departmentService
                .getDepName(schedule.getHoscode(), schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }


    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

}
