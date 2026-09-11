package com.stdmgtsystem.repository;

import com.stdmgtsystem.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long> {

    @Query("SELECT DISTINCT s.department FROM Student s")
    List<String> findDistinctDepartments();

    @Query("SELECT DISTINCT s.city FROM Student s")
    List<String> findDistinctCities();

    Optional<Student> findByEmail(String email);

    boolean existsByRoleRgid(Integer rgid);

    Page<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Student> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<Student> findByDepartmentContainingIgnoreCase(String department, Pageable pageable);

    Page<Student> findByCityContainingIgnoreCase(String city, Pageable pageable);
}


