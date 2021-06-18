package com.ljs.appointment.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ljs.appointment.cmn.client.DictFeignClient;
import com.ljs.appointment.hosp.reponsitory.HospitalReponsitory;
import com.ljs.appointment.hosp.service.HospitalService;
import com.ljs.appointment.model.hosp.Hospital;
import com.ljs.appointment.model.hosp.HospitalSet;
import com.ljs.appointment.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalReponsitory hospitalReponsitory;

    @Autowired
    private DictFeignClient dictFeignClient;

    /**
     * 状态码(0表示未上线,1表示上线) 创建时间 修改时间 是否删除(0代表未删除,1代表已删除)这四个信息不属于上传者需要填写的信息，因此需要在这里进行预设置。
     * @param paramMap 上传的医院基本信息
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        Hospital hospitalExist = hospitalReponsitory.getHospitalByHoscode(hospital.getHoscode());
        if (hospitalExist != null) {
            hospital.setStatus(hospital.getStatus());
            hospital.setCreateTime(hospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalReponsitory.save(hospital);
        }else {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalReponsitory.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospitalByHoscode = hospitalReponsitory.getHospitalByHoscode(hoscode);
        return hospitalByHoscode;
    }

    //医院列表查询
    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建pageable对象
        Pageable pageable = PageRequest.of(page - 1, limit);
        //创建匹配器
        ExampleMatcher matcher = ExampleMatcher.matching().
                withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //转换为hospital对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        //创建对象
        Example<Hospital> example = Example.of(hospital, matcher);
        //调用方法查询
        Page<Hospital> all = hospitalReponsitory.findAll(example, pageable);
        //获取查询list集合，遍历进行封装
        List<Hospital> content = all.getContent();
        content.stream().forEach(item -> {
            this.setHospitalHosType(item);
        });

        return all;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        if (status == 0 ||status ==1 ) {
            Hospital hospital = hospitalReponsitory.findById(id).get();
            //设置要修改的值
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalReponsitory.save(hospital);
        }
    }

    @Override
    public Map<String, Object> getHospById(String id) {
        Map<String, Object> result = new HashMap<>();
        Hospital hospital = hospitalReponsitory.findById(id).get();
        this.setHospitalHosType(hospital);
        //医院基本信息
        result.put("hospital", hospital);
        //单独处理
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }
    //根据医院编号 获得医院名称
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalReponsitory.getHospitalByHoscode(hoscode);
        if (hospital != null) {
            return hospital.getHosname();
        }
        return null;
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalReponsitory.findHospitalByHosnameLike(hosname);
    }

    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String, Object> result = new HashMap<>();
        //医院详情
        Hospital hospital = this.setHospitalHosType(this.getByHoscode(hoscode));
        result.put("hospital", hospital);
        //预约规则
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }

    private Hospital setHospitalHosType(Hospital hospital) {
        //根据dictcode和value值获取医院等级名称
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());
        hospital.getParam().put("hostypeString", hostypeString);
        //查询省，市，地区
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());
        hospital.getParam().put("fullAddress",provinceString + cityString + districtString);
        return hospital;
    }

}
