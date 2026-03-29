package com.hospital.service;

import com.hospital.entity.Department;
import com.hospital.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentService {
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }
    
    public Department findById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }
    
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }
    
    public List<Department> findByNameContaining(String name) {
        return departmentRepository.findByNameContaining(name);
    }
    
    public Department updateDepartment(Department department) {
        return departmentRepository.save(department);
    }
    
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
}