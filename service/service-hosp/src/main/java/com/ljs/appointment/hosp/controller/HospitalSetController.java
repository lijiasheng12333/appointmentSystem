package com.ljs.appointment.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljs.appointment.common.util.MD5;
import com.ljs.appointment.hosp.service.HospitalSetService;
import com.ljs.appointment.model.hosp.HospitalSet;
import com.ljs.appointment.result.Result;
import com.ljs.appointment.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * author ljs
 * create 2021-3-10
 */

@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * 查询医院表所有记录
     *
     * @return list
     */
    @ApiOperation("获取所有医院信息")
    @GetMapping("/findAll")
    public Result findAllHospital() {
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    /**
     * 删除记录（逻辑）
     *
     * @param id 医院记录id号
     * @return boolean
     */
    @ApiOperation("删除医院信息(逻辑)")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable("id") Long id) {
        boolean b = hospitalSetService.removeById(id);
        if (b) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 查询医院记录，以医院名字以及医院编码作为条件。查询可以为null，hostname为医院名字，设置为模糊查询
     */
    @PostMapping("/findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable("current") Long current,
                                  @PathVariable("limit") Long limit,
                                  @RequestBody(required = false) HospitalQueryVo hospitalSetQuery) {
        Page<HospitalSet> page = new Page<>(current, limit);
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hosname = hospitalSetQuery.getHosname();
        String hoscode = hospitalSetQuery.getHoscode();
        if (!StringUtils.isEmpty(hosname)) {
            wrapper.like(" hosname", hosname);
        }
        if (!StringUtils.isEmpty(hoscode)) {
            wrapper.eq("hoscode", hoscode);
        }
        Page<HospitalSet> pageHospSet = hospitalSetService.page(page, wrapper);
        return Result.ok();
    }

    /**
     * 添加医院信息，status为医院状态，1代表可以使用，0代表已锁定不能使用。signkey为密钥，不手动添加
     */
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        hospitalSet.setStatus(1);
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        boolean save = hospitalSetService.save(hospitalSet);
        if (save) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

}
