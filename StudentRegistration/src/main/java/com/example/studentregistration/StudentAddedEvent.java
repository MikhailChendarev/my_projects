package com.example.studentregistration;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class StudentAddedEvent extends ApplicationEvent {

    private final Student student;

    public StudentAddedEvent(Object source, Student student) {
        super(source);
        this.student = student;
    }
}
