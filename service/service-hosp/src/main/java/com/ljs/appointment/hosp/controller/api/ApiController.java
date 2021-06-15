package com.ljs.appointment.hosp.controller.api;

import com.ljs.appointment.common.helper.HttpRequestHelper;
import com.ljs.appointment.common.util.MD5;
import com.ljs.appointment.exception.AppointmentException;
import com.ljs.appointment.hosp.service.HospitalService;
import com.ljs.appointment.hosp.service.HospitalSetService;
import com.ljs.appointment.result.Result;
import com.ljs.appointment.result.ResultCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

    private HospitalSetService hospitalSetService;

    @ApiOperation("上传医院信息")
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String sign = (String)paramMap.get("sign");
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);
        String secureKey = MD5.encrypt(signKey);
        if (hoscode != secureKey) {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
        hospitalService.save(paramMap);
        return Result.ok();
    }

}
