package com.ljs.appointment.hosp.service;


import com.ljs.appointment.model.hosp.Department;
import com.ljs.appointment.vo.hosp.DepartmentQueryVo;
import com.ljs.appointment.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> paramMap);

    /**
     * 查询科室
     * @param page
     * @param limit
     * @param queryVo
     * @return
     */
    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo queryVo);

    void remove(String hoscode, String depcode);

    List<DepartmentVo> findDeptTree(String hoscode);

    //根据科室编号 医院编号 来查询科室名称
    String getDepName(String hoscode, String depcode);
}
