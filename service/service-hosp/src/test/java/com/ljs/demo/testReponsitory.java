package com.ljs.demo;

import com.ljs.appointment.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface testReponsitory extends MongoRepository<Department, String> {
}
