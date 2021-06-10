package com.ljs.appointment.hosp.controller;

import com.ljs.appointment.hosp.service.HospitalSetService;
import com.ljs.appointment.model.hosp.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/findAll")
    public List<HospitalSet> findAllHospital() {
        List<HospitalSet> list = hospitalSetService.list();
        return list;
    }

    /**
     * 删除记录（逻辑）
     *
     * @param id-记录id号
     * @return boolean
     */
    @DeleteMapping("{id}")
    public boolean removeHospSet(@PathVariable("id") Long id) {
        boolean b = hospitalSetService.removeById(id);
        return b;
    }
}
