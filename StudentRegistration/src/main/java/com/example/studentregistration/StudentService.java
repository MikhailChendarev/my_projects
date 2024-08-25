package com.example.studentregistration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    private int nextId = 1;

    private final ApplicationEventPublisher publisher;

    private final Map<Integer, Student> students = new HashMap<>();

    @Value("${app.create-students-on-startup}")
    private boolean createStudentsOnStartup;

    @PostConstruct
    private void addDefaultStudents() {
        if (createStudentsOnStartup) {
            addNewStudent("John", "Doe", 20);
            addNewStudent("Jane", "Doe", 22);
        }
    }

    public StudentService (ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public Student addNewStudent(String firstName, String lastName, int age) {
        Student student = new Student(firstName, lastName, age);
        student.setId(nextId++);
        students.put(student.getId(), student);
        publisher.publishEvent(new StudentAddedEvent(this, student));
        return student;
    }

    public Student deleteById(int id) {
        Student student = students.remove(id);
        publisher.publishEvent(new StudentRemovedEvent(this, id));
        return student;
    }

    public void deleteAllStudents() {
        students.clear();
    }
}
