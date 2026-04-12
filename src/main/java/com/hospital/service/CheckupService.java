package com.hospital.service;

import com.hospital.entity.Checkup;
import com.hospital.repository.CheckupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckupService {

    @Autowired
    private CheckupRepository checkupRepository;

    public List<Checkup> findByDoctorId(Long doctorId) {
        return checkupRepository.findByDoctor_Id(doctorId);
    }

    public List<Checkup> findByPatientId(Long patientId) {
        return checkupRepository.findByPatient_Id(patientId);
    }

    public Checkup findById(Long id) {
        return checkupRepository.findById(id).orElse(null);
    }

    public Checkup save(Checkup checkup) {
        if (checkup.getCreateTime() == null) {
            checkup.setCreateTime(LocalDateTime.now());
        }
        checkup.setUpdateTime(LocalDateTime.now());
        if (checkup.getStatus() == null) {
            checkup.setStatus(0); // 初始状态为待检查
        }
        return checkupRepository.save(checkup);
    }

    public void delete(Long id) {
        checkupRepository.deleteById(id);
    }

    public List<Map<String, Object>> getCheckupsWithPatientName(Long doctorId) {
        List<Checkup> checkups = findByDoctorId(doctorId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Checkup checkup : checkups) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", checkup.getId());
            map.put("type", checkup.getType());
            map.put("description", checkup.getDescription());
            map.put("result", checkup.getResult());
            map.put("status", checkup.getStatus());
            map.put("createTime", checkup.getCreateTime());
            map.put("patientName", checkup.getPatient().getUser().getName());
            result.add(map);
        }

        return result;
    }
    
    public List<Checkup> findByStatus(Integer status) {
        return checkupRepository.findByStatus(status);
    }
    
    public List<Checkup> findAll() {
        return checkupRepository.findAll();
    }
}