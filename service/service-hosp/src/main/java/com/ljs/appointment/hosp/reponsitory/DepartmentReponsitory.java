package com.ljs.appointment.hosp.reponsitory;

import com.ljs.appointment.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentReponsitory extends MongoRepository<Department, String> {
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
