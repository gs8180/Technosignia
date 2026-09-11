package com.stdmgtsystem.controller;

import com.stdmgtsystem.entity.Department;
import com.stdmgtsystem.entity.Role;
import com.stdmgtsystem.entity.Student;
import com.stdmgtsystem.repository.DepartmentRepo;
import com.stdmgtsystem.repository.RoleRepo;
import com.stdmgtsystem.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/students")
public class StudentController 
{

    private final StudentService studentService;
    private final DepartmentRepo departmentRepo;
    private final RoleRepo roleRepo;

    @Autowired
    public StudentController(StudentService studentService, DepartmentRepo departmentRepo, RoleRepo roleRepo) {
        this.studentService = studentService;
        this.departmentRepo = departmentRepo;
        this.roleRepo = roleRepo;
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Student student, HttpSession session) {
        Student loggedInUser = (Student) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (loggedInUser.getRole() == null || loggedInUser.getRole().getRgid() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        if (student.getRole() == null) {
            Role studentRole = roleRepo.findById(2).orElseGet(() -> roleRepo.save(new Role(2, "STUDENT")));
            student.setRole(studentRole);
        }
        return ResponseEntity.ok(studentService.saveStudent(student));
    }

    @GetMapping("/search/name")
    public ResponseEntity<Page<Student>> searchByName(
            @RequestParam("name") String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        
        Page<Student> students = studentService.searchByName(name, page, size, sortBy, sortDir);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search/email")
    public ResponseEntity<Page<Student>> searchByEmail(
            @RequestParam("email") String email,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "email") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        
        Page<Student> students = studentService.searchByEmail(email, page, size, sortBy, sortDir);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search/department")
    public ResponseEntity<Page<Student>> searchByDepartment(
            @RequestParam("department") String department,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        
        Page<Student> students = studentService.searchByDepartment(department, page, size, sortBy, sortDir);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search/city")
    public ResponseEntity<Page<Student>> searchByCity(
            @RequestParam("city") String city,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        
        Page<Student> students = studentService.searchByCity(city, page, size, sortBy, sortDir);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentService.getTotalStudentsCount());
        stats.put("distinctDepartments", studentService.getDistinctDepartments().size());
        stats.put("distinctCities", studentService.getDistinctCities().size());
        
        List<Student> allStudents = studentService.getAllStudents();
        Map<String, Long> deptCounts = new HashMap<>();
        Map<String, Long> cityCounts = new HashMap<>();
        
        for (Student s : allStudents) {
            String dept = s.getDepartment();
            if (dept != null && !dept.trim().isEmpty()) {
                deptCounts.put(dept, deptCounts.getOrDefault(dept, 0L) + 1);
            }
            String city = s.getCity();
            if (city != null && !city.trim().isEmpty()) {
                cityCounts.put(city, cityCounts.getOrDefault(city, 0L) + 1);
            }
        }
        
        stats.put("departmentStats", deptCounts);
        stats.put("cityStats", cityCounts);
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<String>> getAllDepartments() {
        List<String> registeredDepts = departmentRepo.findAll().stream().map(Department::getName).toList();
        List<String> distinctStudentDepts = studentService.getDistinctDepartments();
        java.util.Set<String> allDepts = new java.util.TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        allDepts.addAll(registeredDepts);
        allDepts.addAll(distinctStudentDepts);
        return ResponseEntity.ok(new java.util.ArrayList<>(allDepts));
    }

    @PostMapping("/departments")
    public ResponseEntity<?> addDepartment(@RequestBody Map<String, String> payload, HttpSession session) {
        Student loggedInUser = (Student) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (loggedInUser.getRole() == null || loggedInUser.getRole().getRgid() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        String deptName = payload.get("name");
        if (deptName == null || deptName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Department name is required");
        }
        if (departmentRepo.findByNameIgnoreCase(deptName.trim()).isPresent()) {
            return ResponseEntity.badRequest().body("Department already exists");
        }
        Department dept = new Department();
        dept.setName(deptName.trim());
        departmentRepo.save(dept);
        return ResponseEntity.ok(dept);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student student, HttpSession session) {
        Student loggedInUser = (Student) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (loggedInUser.getRole() == null || loggedInUser.getRole().getRgid() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        return ResponseEntity.ok(studentService.updateStudent(id, student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id, HttpSession session) {
        Student loggedInUser = (Student) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (loggedInUser.getRole() == null || loggedInUser.getRole().getRgid() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}

