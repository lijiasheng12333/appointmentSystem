package com.ljs.demo;


import com.ljs.appointment.model.hosp.Department;
import com.ljs.appointment.vo.hosp.DepartmentVo;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class departmentTest {
    @Resource
    private testReponsitory testReponsitory;

    @Test
    public void findDepartment() {
        String hoscode = "1000_0";
        List<DepartmentVo> result = new ArrayList<>();
        //根据医院编号，查询所有科室信息
        Department departmentQuerry = new Department();
        departmentQuerry.setHoscode(hoscode);
        Example example = Example.of(departmentQuerry);
        List<Department> departmentList = testReponsitory.findAll(example);

        //根据大科室编号bigcode分组  获取大科室下级子科室
        Map<String, List<Department>> departMentMap = departmentList.stream()
                .collect(Collectors.groupingBy(Department::getBigcode));
    }
}
