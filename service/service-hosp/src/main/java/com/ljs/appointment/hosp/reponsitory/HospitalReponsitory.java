package com.ljs.appointment.hosp.reponsitory;

import com.ljs.appointment.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalReponsitory extends MongoRepository<Hospital, String> {

    Hospital getHospitalByHoscode(String hoscode);
}
