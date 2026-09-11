package com.stdmgtsystem;

import com.stdmgtsystem.entity.Student;
import com.stdmgtsystem.repository.StudentRepo;
import com.stdmgtsystem.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StudentSearchAndPaginationTests {

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentRepo.deleteAll();

        List<Student> mockStudents = Arrays.asList(
            new Student(null, "Alice Smith", "alice@example.com", "Computer Science", "New York"),
            new Student(null, "Bob Jones", "bob@example.com", "Mechanical Engineering", "Chicago"),
            new Student(null, "Charlie Brown", "charlie@example.com", "Computer Science", "Los Angeles"),
            new Student(null, "David Wilson", "david@example.com", "Electrical Engineering", "New York"),
            new Student(null, "Eva Green", "eva@example.com", "Information Technology", "San Francisco")
        );
        studentService.saveStudents(mockStudents);
    }

    @Test
    void testSearchByNameWithPaginationAndSorting() {
        Page<Student> page1 = studentService.searchByName("a", 0, 2, "name", "asc");

        assertEquals(4, page1.getTotalElements());
        assertEquals(2, page1.getContent().size());
        assertEquals(2, page1.getTotalPages());

        assertEquals("Alice Smith", page1.getContent().get(0).getName());
        assertEquals("Charlie Brown", page1.getContent().get(1).getName());

        Page<Student> page2 = studentService.searchByName("a", 1, 2, "name", "asc");
        assertEquals("David Wilson", page2.getContent().get(0).getName());
        assertEquals("Eva Green", page2.getContent().get(1).getName());
    }

    @Test
    void testSearchByEmail() {
        Page<Student> page = studentService.searchByEmail("bob@example.com", 0, 5, "id", "asc");
        assertEquals(1, page.getTotalElements());
        assertEquals("Bob Jones", page.getContent().get(0).getName());
    }

    @Test
    void testSearchByDepartment() {
        Page<Student> page = studentService.searchByDepartment("Computer Science", 0, 5, "name", "desc");
        assertEquals(2, page.getTotalElements());
        assertEquals("Charlie Brown", page.getContent().get(0).getName());
        assertEquals("Alice Smith", page.getContent().get(1).getName());
    }

    @Test
    void testSearchByCity() {
        Page<Student> page = studentService.searchByCity("New York", 0, 5, "name", "asc");
        assertEquals(2, page.getTotalElements());
        assertEquals("Alice Smith", page.getContent().get(0).getName());
        assertEquals("David Wilson", page.getContent().get(1).getName());
    }
}

