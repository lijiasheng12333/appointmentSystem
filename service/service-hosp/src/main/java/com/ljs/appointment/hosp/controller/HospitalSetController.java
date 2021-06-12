package com.ljs.appointment.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljs.appointment.common.util.MD5;
import com.ljs.appointment.hosp.service.HospitalSetService;
import com.ljs.appointment.model.hosp.HospitalSet;
import com.ljs.appointment.result.Result;
import com.ljs.appointment.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
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
@Api("医院管理信息设置")
@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
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
     * @param current 当前页
     * @param limit 每页限制数量
     * @return pageHospSet 查询结果
     */
    @ApiOperation("查询医院信息记录(分页)")
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
        return Result.ok(pageHospSet);
    }

    /**
     * 添加医院信息，status为医院状态，1代表可以使用，0代表已锁定不能使用。signkey为密钥，不手动添加
     * @param hospitalSet
     */
    @ApiOperation("添加医院信息")
    @PostMapping("/saveHospitalSet")
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

    /**
     * 根据id查询医院信息
     * @param id 医院id
     */
    @ApiOperation("查询医院信息(id)")
    @GetMapping("/getHospSet/{id}")
    public Result getHospSet(@PathVariable("id") Long id) {
        HospitalSet byId = hospitalSetService.getById(id);
        return Result.ok(byId);
    }

    /**
     * 修改医院信息
     * @param hospitalSet
     */
    @ApiOperation("修改医院信息")
    @PostMapping("/updateHospSet")
    public Result updateHospSet(@RequestBody HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 根据id批量删除医院信息
     * @param idList 批量删除的医院的所有id
     */
    @ApiOperation("删除医院记录")
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospSet(@RequestBody List<Long> idList) {
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    /**
     * 对医院状态进行锁定设置
     * @param id 医院id
     * @param status 状态码 1代表锁定，0代表未锁定
     */
    @ApiOperation("对医院状态进行锁定操作")
    @PutMapping("/lockHospSet/{id}/{status}")
    public Result lockHospSet(@PathVariable("id") Long id,
                              @PathVariable("status") Integer status) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 发送签名key，最后以短信的形式进行发送
     * @param id 医院id
     */
    @ApiOperation("发送签名key")
    @GetMapping("/sendKey/{id}")
    public Result sendKey(@PathVariable("id") Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //这里有短信发送服务 目前没做 留出来
        return Result.ok();
    }

}
