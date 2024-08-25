package com.example.studentregistration;

import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;

@Setter
@Getter
public class Student {

    private int id;

    private String firstName;

    private String lastName;

    private int age;

    public Student(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    @Override
    public String toString() {
        return MessageFormat.format("\nStudent with id {0}: firstname - {1}, lastname - {2}, age - {3}\n",
                id, firstName, lastName, age);
    }

}
