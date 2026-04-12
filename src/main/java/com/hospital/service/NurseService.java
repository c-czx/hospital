package com.hospital.service;

import com.hospital.entity.*;
import com.hospital.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NurseService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private NurseRepository nurseRepository;

    // 获取所有护士列表
    public List<Nurse> findAll() {
        return nurseRepository.findAll();
    }

    // 根据 ID 查找护士
    public Nurse findById(Integer id) {
        return nurseRepository.findById(id).orElse(null);
    }

    // 保存护士信息
    @Transactional
    public void saveNurse(Nurse nurse) {
        nurseRepository.save(nurse);
    }

    // 更新护士信息
    @Transactional
    public void updateNurse(Nurse nurse) {
        nurseRepository.save(nurse);
    }

    // 删除护士
    @Transactional
    public void deleteNurse(Integer id) {
        nurseRepository.deleteById(id);
    }

    // 查看患者详细档案
    public Patient getPatientDetail(Long patientId) {
        return patientRepository.findById(patientId).orElse(null);
    }
}