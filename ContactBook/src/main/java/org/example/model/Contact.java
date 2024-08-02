package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Contact {

    private String name;

    private String phoneNumber;

    private String email;

}
