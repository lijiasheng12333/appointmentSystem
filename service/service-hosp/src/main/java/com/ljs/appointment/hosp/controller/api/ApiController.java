package com.ljs.appointment.hosp.controller.api;

import com.ljs.appointment.common.helper.HttpRequestHelper;
import com.ljs.appointment.common.util.MD5;
import com.ljs.appointment.exception.AppointmentException;
import com.ljs.appointment.hosp.service.HospitalService;
import com.ljs.appointment.hosp.service.HospitalSetService;
import com.ljs.appointment.model.hosp.Hospital;
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

    @Autowired
    private HospitalSetService hospitalSetService;

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
}
