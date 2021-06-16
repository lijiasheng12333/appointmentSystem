package com.ljs.appointment.hosp.controller.api;


import com.ljs.appointment.common.helper.HttpRequestHelper;
import com.ljs.appointment.common.util.MD5;
import com.ljs.appointment.exception.AppointmentException;
import com.ljs.appointment.hosp.service.DepartmentService;
import com.ljs.appointment.hosp.service.HospitalService;
import com.ljs.appointment.hosp.service.HospitalSetService;
import com.ljs.appointment.hosp.service.ScheduleService;
import com.ljs.appointment.model.hosp.Department;
import com.ljs.appointment.model.hosp.Hospital;
import com.ljs.appointment.model.hosp.Schedule;
import com.ljs.appointment.result.Result;
import com.ljs.appointment.result.ResultCodeEnum;
import com.ljs.appointment.vo.hosp.DepartmentQueryVo;
import com.ljs.appointment.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api("医院管理Api接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;
    /**
     * 上传医院信息,通过签名验证识别身份,拿到的签名均使用MD5进行加密
     * 对上传的图片进行了格式修正
     * @param request 请求
     * @return
     */
    @ApiOperation("上传医院信息")
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String sign = (String)paramMap.get("sign");
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);
        String secureKey = MD5.encrypt(signKey);
        if (!sign.equals(secureKey)) {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
        String logoData = (String)paramMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        paramMap.put("logoData", logoData);

        hospitalService.save(paramMap);
        return Result.ok();
    }
    @ApiOperation("查询医院信息")
    @PostMapping("/hospital/show")
    public Result showHospital(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String sign = (String) paramMap.get("sign");
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);
        String secureKey = MD5.encrypt(signKey);
        if (!sign.equals(secureKey)) {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }
    @ApiOperation("上传科室信息")
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String sign = (String) paramMap.get("sign");
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);
        String secureKey = MD5.encrypt(signKey);
        if (!sign.equals(secureKey)) {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
        departmentService.save(paramMap);
        return Result.ok();
    }
    @ApiOperation("查询科室信息")
    @PostMapping("/department/list")
    public Result findDepartment(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) paramMap.get("hoscode");
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1: Integer.parseInt((String)paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1: Integer.parseInt((String)paramMap.get("limit"));
        String sign = (String) paramMap.get("sign");
        String signKey = hospitalSetService.getSignKey(hoscode);
        String secureKey = MD5.encrypt(signKey);
        if (!sign.equals(secureKey)) {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
        DepartmentQueryVo queryVo = new DepartmentQueryVo();
        queryVo.setHoscode(hoscode);
        Page<Department> pageModel = departmentService.findPageDepartment(page, limit, queryVo);
        return Result.ok(pageModel);
    }
    @ApiOperation("删除科室信息")
    @PostMapping("/department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        String sign = (String) paramMap.get("sign");
        String signKey = hospitalSetService.getSignKey(hoscode);
        String secureKey = MD5.encrypt(signKey);
        if (!sign.equals(secureKey)) {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    @ApiOperation("排班信息")
    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) paramMap.get("hoscode");
        String sign = (String) paramMap.get("sign");
        String signKey = hospitalSetService.getSignKey(hoscode);
        String secureKey = MD5.encrypt(signKey);
        if (!sign.equals(secureKey)) {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
        scheduleService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation("查询排版信息")
    @PostMapping("/schedule/list")
    public Result findSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1: Integer.parseInt((String)paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1: Integer.parseInt((String)paramMap.get("limit"));

        String sign = (String) paramMap.get("sign");
        String signKey = hospitalSetService.getSignKey(hoscode);
        String secureKey = MD5.encrypt(signKey);
        if (!sign.equals(secureKey)) {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }

        ScheduleQueryVo queryVo = new ScheduleQueryVo();
        queryVo.setHoscode(hoscode);
        queryVo.setDepcode(depcode);
        Page<Schedule> pageModel = scheduleService.findPageDepartment(page, limit, queryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation("删除排班信息")
    @PostMapping("/schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) paramMap.get("hoscode");
        String hosScheduleId = (String) paramMap.get("hosScheduleId");

        String sign = (String) paramMap.get("sign");
        String signKey = hospitalSetService.getSignKey(hoscode);
        String secureKey = MD5.encrypt(signKey);
        if (!sign.equals(secureKey)) {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }

        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }

}
