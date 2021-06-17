package com.ljs.appointment.cmn.controller;

import com.ljs.appointment.cmn.service.DictService;
import com.ljs.appointment.model.cmn.Dict;
import com.ljs.appointment.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author ljs
 * @create 2021-3-13
 */
@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 查询子数据列表
     * @param id 数据id
     * @return list
     */
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("/findChildData/{id}")
    public Result findChildData(@PathVariable("id") Long id) {
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }

    /**
     * 进行数据导出
     * @param response
     */
    @ApiOperation("导出数据")
    @GetMapping("/export")
    public void exportData(HttpServletResponse response) {
        dictService.exportData(response);
    }

    @ApiOperation("导入数据")
    @PostMapping("/importData")
    public Result importData(MultipartFile file) {
        dictService.importData(file);
        return Result.ok();
    }

    //根据dictcode和value进行查询
    @GetMapping("/getName/{dictCode}/{value}")
    public String getName(@PathVariable("dictCode") String dictCode,
                          @PathVariable("value") String value) {
        String dictName = dictService.getDictName(dictCode, value);
        return dictName;
    }

    //根据value进行查询
    @GetMapping("/getName/{value}")
    public String getName(@PathVariable("value") String value) {
        String dictName = dictService.getDictName("", value);
        return dictName;
    }

    //根据dictCode获取下级节点
    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping(value = "/findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable("dictCode") String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }

}
