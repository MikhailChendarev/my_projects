package com.example.studentregistration;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StudentEventListeners {

    @EventListener
    public void handleStudentAdded(StudentAddedEvent event) {
        System.out.println("Student created: " + event.getStudent());
    }

    @EventListener
    public void handleStudentRemoved(StudentRemovedEvent event) {
        System.out.println("Student removed ID: " + event.getStudentId());
    }
}
