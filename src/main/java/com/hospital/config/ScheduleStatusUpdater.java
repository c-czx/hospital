package com.hospital.config;

import com.hospital.entity.Schedule;
import com.hospital.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务：更新过期号源状态为已过期
 * 每小时执行一次
 */
@Component
public class ScheduleStatusUpdater {
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Scheduled(fixedRate = 3600000)
    public void updateExpiredSchedules() {
        LocalDateTime now = LocalDateTime.now();
        List<Schedule> schedules = scheduleRepository.findAll();
        
        int updatedCount = 0;
        for (Schedule schedule : schedules) {
            if (schedule.getStatus() == 1 && schedule.getEndTime() != null && now.isAfter(schedule.getEndTime())) {
                schedule.setStatus(2);
                scheduleRepository.save(schedule);
                updatedCount++;
            }
        }
        
        if (updatedCount > 0) {
            System.out.println("【定时任务】已更新 " + updatedCount + " 个过期号源状态");
        }
    }
}
