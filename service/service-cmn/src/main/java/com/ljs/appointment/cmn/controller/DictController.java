package com.ljs.appointment.cmn.controller;

import com.ljs.appointment.cmn.service.DictService;
import com.ljs.appointment.model.cmn.Dict;
import com.ljs.appointment.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ljs
 * @create 2021-3-13
 */
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @GetMapping("/findchildData/{id}")
    public Result findChildData(@PathVariable("id") Long id) {
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }
}
