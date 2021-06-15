package com.ljs.appointment.hosp.controller.api;

import com.ljs.appointment.common.helper.HttpRequestHelper;
import com.ljs.appointment.hosp.service.HospitalService;
import com.ljs.appointment.result.Result;
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

    @ApiOperation("上传医院信息")
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        hospitalService.save(paramMap);
        return Result.ok();
    }

}
