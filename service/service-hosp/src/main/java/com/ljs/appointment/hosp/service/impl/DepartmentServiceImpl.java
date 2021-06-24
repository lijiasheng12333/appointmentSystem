package com.ljs.appointment.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljs.appointment.hosp.reponsitory.DepartmentReponsitory;
import com.ljs.appointment.hosp.service.DepartmentService;
import com.ljs.appointment.model.hosp.Department;
import com.ljs.appointment.vo.hosp.DepartmentQueryVo;
import com.ljs.appointment.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        List<DepartmentVo> result = new ArrayList<>();
        //根据医院编号，查询所有科室信息
        Department departmentQuerry = new Department();
        departmentQuerry.setHoscode(hoscode);
        Example example = Example.of(departmentQuerry);
        List<Department> departmentList = departmentReponsitory.findAll(example);

        //根据大科室编号bigcode分组  获取大科室下级子科室
        Map<String, List<Department>> departMentMap = departmentList.stream()
                                                    .collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合
        for (Map.Entry<String, List<Department>> entry : departMentMap.entrySet()) {
            //大科室编号
            String bigcode = entry.getKey();
            //得到大科室编号对应的全部数据
            List<Department> department1List = entry.getValue();
            //封装大科室
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigcode);
            departmentVo1.setDepname(department1List.get(0).getBigname());
            //封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for(Department department: department1List) {
                DepartmentVo departmentVo2 =  new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode());
                departmentVo2.setDepname(department.getDepname());
                //封装到list集合
                children.add(departmentVo2);
            }
            //把小科室list集合放到大科室children里面
            departmentVo1.setChildren(children);
            //放到最终result里面
            result.add(departmentVo1);
        }
        //返回
        return result;
    }

    //根据科室编号 医院编号 来查询科室名称
    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentReponsitory.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            return department.getDepname();
        }
        return null;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentReponsitory.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }
}
