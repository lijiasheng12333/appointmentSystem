package com.ljs.appointment.hosp.controller;

import com.ljs.appointment.hosp.service.HospitalService;
import com.ljs.appointment.model.hosp.Hospital;
import com.ljs.appointment.result.Result;
import com.ljs.appointment.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    //查询医院列表(条件查询分页)
    @GetMapping("/list/{page}/{limit}")
    public Result listHosp(@PathVariable("page") Integer page,
                           @PathVariable("limit") Integer limit,
                           HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> pageModel = hospitalService.selectPage(page, limit, hospitalQueryVo);
        return Result.ok(pageModel);
    }
}
