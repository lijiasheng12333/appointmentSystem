package com.ljs.appointment.hosp.service;


import com.ljs.appointment.model.hosp.Department;
import com.ljs.appointment.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;

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
}
