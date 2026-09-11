package com.stdmgtsystem.service.impl;

import com.stdmgtsystem.entity.Student;
import com.stdmgtsystem.repository.StudentRepo;
import com.stdmgtsystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepo studentRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public StudentServiceImpl(StudentRepo studentRepo) {
        this.studentRepo = studentRepo;
    }

    @Transactional
    @Override
    public void clearAllStudents() {
        studentRepo.deleteAll();
        entityManager.createNativeQuery("ALTER TABLE Students AUTO_INCREMENT = 101").executeUpdate();
    }

    @Override
    public long getTotalStudentsCount() {
        return studentRepo.count();
    }

    @Override
    public List<String> getDistinctDepartments() {
        return studentRepo.findDistinctDepartments();
    }

    @Override
    public List<String> getDistinctCities() {
        return studentRepo.findDistinctCities();
    }

    @Override
    public Student saveStudent(Student student) {
        return studentRepo.save(student);
    }

    @Override
    public List<Student> saveStudents(List<Student> students) {
        return studentRepo.saveAll(students);
    }

    @Override
    public Page<Student> searchByName(String name, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return studentRepo.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Page<Student> searchByEmail(String email, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepo.findByEmailContainingIgnoreCase(email, pageable);
    }

    @Override
    public Page<Student> searchByDepartment(String department, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepo.findByDepartmentContainingIgnoreCase(department, pageable);
    }

    @Override
    public Page<Student> searchByCity(String city, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepo.findByCityContainingIgnoreCase(city, pageable);
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepo.findById(id).orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    @Override
    @Transactional
    public Student updateStudent(Long id, Student studentDetails) {
        Student student = studentRepo.findById(id).orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        student.setName(studentDetails.getName());
        student.setEmail(studentDetails.getEmail());
        student.setDepartment(studentDetails.getDepartment());
        student.setCity(studentDetails.getCity());
        return studentRepo.save(student);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        studentRepo.deleteById(id);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }
}
