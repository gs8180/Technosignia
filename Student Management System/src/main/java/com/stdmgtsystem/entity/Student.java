package com.stdmgtsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Students")
public class Student 
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq")
    @SequenceGenerator(name = "student_seq", sequenceName = "student_sequence", initialValue = 101, allocationSize = 1)
    private Long id;

    private String name;

    private String email;

    private String department;

    private String city;

    private String password;

    @ManyToOne
    @JoinColumn(name = "rgid")
    private Role role;

    public Student(Long id, String name, String email, String department, String city) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.city = city;
    }
}
