package com.stdmgtsystem.service;

import com.stdmgtsystem.entity.Student;
import org.springframework.data.domain.Page;
import java.util.List;

public interface StudentService {

    Student saveStudent(Student student);

    List<Student> saveStudents(List<Student> students);

    void clearAllStudents();

    long getTotalStudentsCount();
    List<String> getDistinctDepartments();
    List<String> getDistinctCities();

    Page<Student> searchByName(String name, int page, int size, String sortBy, String sortDir);

    Page<Student> searchByEmail(String email, int page, int size, String sortBy, String sortDir);

    Page<Student> searchByDepartment(String department, int page, int size, String sortBy, String sortDir);

    Page<Student> searchByCity(String city, int page, int size, String sortBy, String sortDir);

    Student getStudentById(Long id);

    Student updateStudent(Long id, Student student);

    void deleteStudent(Long id);

    java.util.List<Student> getAllStudents();
}

