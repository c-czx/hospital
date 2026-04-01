package com.hospital.service;

import com.hospital.entity.Advice;
import com.hospital.repository.AdviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdviceService {

    @Autowired
    private AdviceRepository adviceRepository;

    public List<Advice> findByDoctorId(Long doctorId) {
        return adviceRepository.findByDoctorId(doctorId);
    }

    public List<Map<String, Object>> getAdvicesWithPatientName(Long doctorId) {
        List<Advice> advices = findByDoctorId(doctorId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Advice advice : advices) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", advice.getId());
            map.put("content", advice.getContent());
            map.put("status", advice.getStatus());
            map.put("createTime", advice.getCreateTime());
            map.put("patientName", advice.getUser().getName());
            result.add(map);
        }

        return result;
    }

    public Advice findById(Long id) {
        return adviceRepository.findById(id).orElse(null);
    }

    public Advice save(Advice advice) {
        if (advice.getCreateTime() == null) {
            advice.setCreateTime(LocalDateTime.now());
        }
        advice.setUpdateTime(LocalDateTime.now());
        return adviceRepository.save(advice);
    }

    public void delete(Long id) {
        adviceRepository.deleteById(id);
    }
}
