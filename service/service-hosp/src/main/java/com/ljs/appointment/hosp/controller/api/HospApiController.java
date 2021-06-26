package com.ljs.appointment.hosp.controller.api;

import com.ljs.appointment.hosp.service.DepartmentService;
import com.ljs.appointment.hosp.service.HospitalService;
import com.ljs.appointment.hosp.service.HospitalSetService;
import com.ljs.appointment.hosp.service.ScheduleService;
import com.ljs.appointment.model.hosp.Hospital;
import com.ljs.appointment.model.hosp.Schedule;
import com.ljs.appointment.result.Result;
import com.ljs.appointment.vo.hosp.DepartmentVo;
import com.ljs.appointment.vo.hosp.HospitalQueryVo;
import com.ljs.appointment.vo.hosp.ScheduleOrderVo;
import com.ljs.appointment.vo.order.SignInfoVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp/hospital")
public class HospApiController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("查询医院列表")
    @GetMapping("/findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable("page") Integer page,
                               @PathVariable("limit") Integer limit,
                               HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> hospitals = hospitalService.selectPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);
    }

    @ApiOperation("根据医院名称查询")
    @GetMapping("/findByHosname/{hosname}")
    public Result findByHosname(@PathVariable("hosname") String hosname) {
        List<Hospital> list = hospitalService.findByHosname(hosname);
        return Result.ok(list);
    }

    @ApiOperation(value = "根据编号获取科室列表")
    @GetMapping("/department/{hoscode}")
    public Result index(@PathVariable("hoscode") String hoscode) {
        List<DepartmentVo> deptTree = departmentService.findDeptTree(hoscode);
        return Result.ok(deptTree);
    }

    @ApiOperation(value = "医院预约挂号详情")
    @GetMapping("/{hoscode}")
    public Result item(@PathVariable("hoscode") String hoscode) {
        Map<String, Object> map = hospitalService.item(hoscode);
        return Result.ok(map);
    }

    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingSchedule(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Integer limit,
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode) {
        return Result.ok(scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode));
    }

    @ApiOperation(value = "获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode,
            @ApiParam(name = "workDate", value = "排班日期", required = true)
            @PathVariable String workDate) {
        return Result.ok(scheduleService.getDetailSchedule(hoscode, depcode, workDate));
    }

    @ApiOperation(value = "根据排班id获取排班数据")
    @GetMapping("/getSchedule/{scheduleId}")
    public Result getSchedule(@PathVariable("scheduleId") String scheduleId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return Result.ok(schedule);
    }

    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable("scheduleId") String scheduleId) {
        return scheduleService.getScheduleOrderVo(scheduleId);
    }

    @ApiOperation(value = "获取医院签名信息")
    @GetMapping("inner/getSignInfoVo/{hoscode}")
    public SignInfoVo getSignInfoVo(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable("hoscode") String hoscode) {
        return hospitalSetService.getSignInfoVo(hoscode);
    }

}
