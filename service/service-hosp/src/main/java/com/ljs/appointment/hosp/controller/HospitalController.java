package com.ljs.appointment.hosp.controller;

import com.ljs.appointment.hosp.service.HospitalService;
import com.ljs.appointment.model.hosp.Hospital;
import com.ljs.appointment.result.Result;
import com.ljs.appointment.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    //查询医院列表(条件查询分页)
    @GetMapping("/list/{page}/{limit}")
    public Result listHosp(@PathVariable("page") Integer page,
                           @PathVariable("limit") Integer limit,
                           HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> pageModel = hospitalService.selectPage(page, limit, hospitalQueryVo);
        List<Hospital> content = pageModel.getContent();
        long totalElements = pageModel.getTotalElements();

        return Result.ok(pageModel);
    }
    //更新医院上线状态
    @GetMapping("/updateStatus/{id}/{status}")
    public Result lock(@PathVariable("id") String id,
                       @PathVariable("status") Integer status) {
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }

    //显示医院详情
    @GetMapping("/showHospDetail/{id}")
    public Result showHospDetail(@PathVariable("id") String id) {
        Map<String, Object> map = hospitalService.getHospById(id);
        return Result.ok(map);
    }
}
