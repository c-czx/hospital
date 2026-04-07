package com.hospital.service;

import com.hospital.entity.*;
import com.hospital.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class NurseService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private NurseRepository nurseRepository;

    // 1. 护士：查询待处理队列 (例如：状态为 "挂号成功" 且未缴费的记录)
    public List<Map<String, Object>> getPendingQueue() {
        List<Registration> list = registrationRepository.findByStatus("挂号成功");
        return convertToDto(list); // 转换为前端需要的格式
    }

    // 2. 护士：协助患者缴费 (核心功能)
    @Transactional
    public Map<String, String> handlePayment(Long regId) {
        Map<String, String> result = new HashMap<>();

        Optional<Registration> optional = registrationRepository.findById(regId);
        if (optional.isPresent()) {
            Registration reg = optional.get();

            // 业务逻辑1：更新挂号单状态为 "已缴费"
            reg.setStatus("已缴费");
            registrationRepository.save(reg);

            // 业务逻辑2：生成一条账单记录 (Billing)
            Billing bill = new Billing();
            bill.setUser(reg.getPatient().getUser()); // 关联患者用户
            bill.setType("挂号费");
            bill.setAmount(reg.getFee()); // 假设挂号单里有费用字段
            bill.setStatus("已支付");
            bill.setCreateTime(LocalDateTime.now());
            billingRepository.save(bill);

            result.put("code", "200");
            result.put("msg", "缴费成功，已通知医生诊室");
        } else {
            result.put("code", "404");
            result.put("msg", "未找到挂号记录");
        }
        return result;
    }

    // 3. 护士：查看患者详细档案
    public Patient getPatientDetail(Long patientId) {
        return patientRepository.findById(patientId).orElse(null);
    }

    // 辅助方法：将 Entity 转换为简单的 Map 供前端展示
    private List<Map<String, Object>> convertToDto(List<Registration> list) {
        List<Map<String, Object>> dtos = new ArrayList<>();
        for (Registration reg : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("regId", reg.getRegId());
            map.put("patientName", reg.getPatient().getUser().getName());
            map.put("dept", reg.getDepartment().getName());
            map.put("time", reg.getRegTime());
            map.put("fee", reg.getFee());
            dtos.add(map);
        }
        return dtos;
    }
}