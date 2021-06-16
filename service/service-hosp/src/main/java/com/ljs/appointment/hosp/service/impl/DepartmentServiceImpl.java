package com.ljs.appointment.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljs.appointment.hosp.reponsitory.DepartmentReponsitory;
import com.ljs.appointment.hosp.service.DepartmentService;
import com.ljs.appointment.model.hosp.Department;
import com.ljs.appointment.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo queryVo) {
        //创建pageable对象，设置当前页和记录数
        //0为第一页
        Pageable pageable = PageRequest.of(page - 1, limit);
        //创建example对象
        Department department = new Department();
        BeanUtils.copyProperties(queryVo, department);
        department.setIsDeleted(0);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example = Example.of(department, matcher);
        Page<Department> all = departmentReponsitory.findAll(example, pageable);
        return all;
    }

    //删除
    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentReponsitory.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentReponsitory.deleteById(department.getId());
        }
    }
}
