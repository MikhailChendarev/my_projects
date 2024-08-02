package org.example.service;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.model.Contact;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private final String filePath = "C:/Users/User/Desktop/searchEngine/ContactBook/src/main/resources/contacts.txt";

    private Set<Contact> contacts = new HashSet<>();

    public Set<Contact> getAllContacts() {
        return contacts;
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    public void removeContactByEmail(String email) {
        contacts.removeIf(contact -> contact.getEmail().equals(email));
    }

    @PreDestroy
    public void saveContactsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Contact contact : contacts) {
                writer.write(contact.getName() + ";" + contact.getPhoneNumber() + ";" + contact.getEmail());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void loadContactsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    Contact contact = new Contact(parts[0], parts[1], parts[2]);
                    contacts.add(contact);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Welcome to the Contact Book Application!");
        System.out.println("Available commands:");
        System.out.println("1 - Add a new contact");
        System.out.println("2 - Search contact by name");
        System.out.println("3 - Search contact by phone number");
        System.out.println("4 - Search contact by email");
        System.out.println("5 - Get all contacts");
        System.out.println("6 - Clear all contacts");
        System.out.println("7 - Delete contact by email");
    }

    public Set<Contact> searchContactByName(String name) {
        return contacts.stream()
                .filter(contact -> contact.getName().equals(name))
                .collect(Collectors.toSet());
    }

    public Set<Contact> searchContactByPhoneNumber(String phoneNumber) {
        return contacts.stream()
                .filter(contact -> contact.getPhoneNumber().equals(phoneNumber))
                .collect(Collectors.toSet());
    }

    public Set<Contact> searchContactByEmail(String email) {
        return contacts.stream()
                .filter(contact -> contact.getEmail().equals(email))
                .collect(Collectors.toSet());
    }

    public void clearContacts() {
        contacts.clear();
    }

}
