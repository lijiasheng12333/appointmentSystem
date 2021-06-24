package com.ljs.appointment.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljs.appointment.cmn.client.DictFeignClient;
import com.ljs.appointment.enums.DictEnum;
import com.ljs.appointment.model.user.Patient;
import com.ljs.appointment.user.mapper.PatientMapper;
import com.ljs.appointment.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public List<Patient> findAllByUserId(Long userId) {
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Patient> patientList = baseMapper.selectList(wrapper);
        patientList.stream().forEach(item -> {
            //通过远程调用得到编码具体内容  其他参数封装
            this.packPatient(item);
        });
        return patientList;
    }

    @Override
    public Patient getPatientById(Long id) {
        Patient patient = baseMapper.selectById(id);
        this.packPatient(patient);
        return patient;
    }

    //patient对象其他参数封装
    private Patient packPatient(Patient patient) {
        //根据证件编码  获取证件类型具体值(身份证/户口本)
        String certificatesTypeString = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode()
                , patient.getCertificatesType());

        //联系人证件类型
        String contactsCertificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getContactsCertificatesType());
        //省
        String provinceString = dictFeignClient.getName(patient.getProvinceCode());
        //市
        String cityString = dictFeignClient.getName(patient.getCityCode());
        //区
        String districtString = dictFeignClient.getName(patient.getDistrictCode());
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;
    }
}
