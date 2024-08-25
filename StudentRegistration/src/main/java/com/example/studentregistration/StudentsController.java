package com.example.studentregistration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
public class StudentsController {

    @Autowired
    private StudentService studentService;

    @ShellMethod(value = "Students list", key = {"get all", "ga"})
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @ShellMethod(value = "Add a new student to list", key = {"add", "a"})
    public void addNewStudent(@ShellOption(value = {"-f", "--firstname"}) String firstName,
                              @ShellOption(value = {"-l", "--lastname"}) String lastName,
                              @ShellOption(value = {"-a", "--age"}) int age) {
        Student student = studentService.addNewStudent(firstName, lastName, age);
    }

    @ShellMethod(value = "Delete student by id", key = {"delete", "d"})
    public void deleteStudentById(@ShellOption(value = {"--id", "-i"}) int id) {
        Student student = studentService.deleteById(id);
    }

    @ShellMethod(value = "Remove all students", key = {"remove", "r"})
    public String removeAllStudents() {
        studentService.deleteAllStudents();
        return "All students have been removed";
    }
}
