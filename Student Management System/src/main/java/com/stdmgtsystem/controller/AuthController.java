package com.stdmgtsystem.controller;

import com.stdmgtsystem.entity.Department;
import com.stdmgtsystem.entity.Role;
import com.stdmgtsystem.entity.Student;
import com.stdmgtsystem.repository.DepartmentRepo;
import com.stdmgtsystem.repository.RoleRepo;
import com.stdmgtsystem.repository.StudentRepo;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final StudentRepo studentRepo;
    private final RoleRepo roleRepo;
    private final DepartmentRepo departmentRepo;

    @Autowired
    public AuthController(StudentRepo studentRepo, RoleRepo roleRepo, DepartmentRepo departmentRepo) {
        this.studentRepo = studentRepo;
        this.roleRepo = roleRepo;
        this.departmentRepo = departmentRepo;
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/dashboard";
        }
        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session, 
                                @RequestParam(value = "registered", required = false) String registered,
                                @RequestParam(value = "logout", required = false) String logout) {
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", new Student());
        if (registered != null) {
            model.addAttribute("success", "Registration successful! Please log in.");
        }
        if (logout != null) {
            model.addAttribute("info", "You have been logged out successfully.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") Student user,
                            @RequestParam(value = "loginRole", required = false) String loginRole,
                            HttpSession session,
                            Model model) {
        Optional<Student> existingUser = studentRepo.findByEmail(user.getEmail());
        if (existingUser.isPresent() && existingUser.get().getPassword().equals(user.getPassword())) {
            Student dbUser = existingUser.get();
            if (loginRole != null) {
                if (loginRole.equals("admin") && (dbUser.getRole() == null || dbUser.getRole().getRgid() != 1)) {
                    model.addAttribute("error", "Access denied. You do not have administrator privileges.");
                    return "login";
                } else if (loginRole.equals("student") && (dbUser.getRole() == null || dbUser.getRole().getRgid() != 2)) {
                    model.addAttribute("error", "Access denied. This account is registered as an administrator.");
                    return "login";
                }
            }
            session.setAttribute("loggedInUser", dbUser);
            return "redirect:/dashboard";
        }
        model.addAttribute("error", "Invalid email or password.");
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", new Student());
        List<String> registeredDepts = departmentRepo.findAll().stream().map(Department::getName).toList();
        List<String> distinctStudentDepts = studentRepo.findDistinctDepartments();
        java.util.Set<String> allDepts = new java.util.TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        allDepts.addAll(registeredDepts);
        allDepts.addAll(distinctStudentDepts);
        if (allDepts.isEmpty()) {
            allDepts.add("Computer Science");
            allDepts.add("Information Technology");
            allDepts.add("Mechanical Engineering");
            allDepts.add("Electrical Engineering");
            for (String deptName : allDepts) {
                Department dept = new Department();
                dept.setName(deptName);
                departmentRepo.save(dept);
            }
        }
        model.addAttribute("departments", allDepts);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") Student user, Model model) {
        Optional<Student> existingUser = studentRepo.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            model.addAttribute("error", "An account with this email already exists.");
            return "register";
        }
        Role role;
        if (!studentRepo.existsByRoleRgid(1)) {
            role = roleRepo.findById(1).orElseGet(() -> roleRepo.save(new Role(1, "ADMIN")));
        } else {
            role = roleRepo.findById(2).orElseGet(() -> roleRepo.save(new Role(2, "STUDENT")));
        }
        user.setRole(role);
        studentRepo.save(user);
        return "redirect:/login?registered=true";
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Student loggedInUser = (Student) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", loggedInUser);
        return "dashboard";
    }

    @GetMapping("/students")
    public String showStudents(HttpSession session, Model model) {
        Student loggedInUser = (Student) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", loggedInUser);
        return "students";
    }

    @GetMapping("/departments")
    public String showDepartments(HttpSession session, Model model) {
        Student loggedInUser = (Student) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", loggedInUser);
        return "departments";
    }

    @GetMapping("/reports")
    public String showReports(HttpSession session, Model model) {
        Student loggedInUser = (Student) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        if (loggedInUser.getRole() == null || loggedInUser.getRole().getRgid() != 1) {
            return "redirect:/dashboard";
        }
        model.addAttribute("currentUser", loggedInUser);
        return "reports";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/?logout=true";
    }
}
