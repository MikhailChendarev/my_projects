package com.example.studentregistration;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class StudentRemovedEvent extends ApplicationEvent {

    private final int studentId;

    public StudentRemovedEvent(Object source, int studentId) {
        super(source);
        this.studentId = studentId;
    }
}
