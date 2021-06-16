package com.ljs.appointment.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljs.appointment.hosp.reponsitory.DepartmentReponsitory;
import com.ljs.appointment.hosp.service.DepartmentService;
import com.ljs.appointment.model.hosp.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentReponsitory departmentReponsitory;

    @Override
    public void save(Map<String, Object> paramMap) {
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramMapString, Department.class);
        Department departmentExist = departmentReponsitory
                .getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        if (departmentExist != null) {
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentReponsitory.save(departmentExist);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentReponsitory.save(department);
        }
    }
}
