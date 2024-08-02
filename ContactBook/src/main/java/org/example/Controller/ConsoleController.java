package org.example.Controller;

import org.example.model.Contact;
import org.example.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Scanner;
import java.util.Set;

@ShellComponent
public class ConsoleController {

    @Autowired
    private ContactService contactService;

    @ShellMethod(key = "1", value = "1 - Add a new contact")
    public String addContact() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a name:");
        String name = scanner.nextLine();

        System.out.println("Enter a phone number:");
        String phoneNumber = scanner.nextLine();

        System.out.println("Enter an email:");
        String email = scanner.nextLine();

        Contact contact = new Contact(name, phoneNumber, email);
        contactService.addContact(contact);
        return "Contact added successfully";
    }

    @ShellMethod(key = "2", value = "2 - Search contact by name")
    public Set<Contact> loadContactByName(@ShellOption(help = "Enter a name") String name) {
        return contactService.searchContactByName(name);
    }

    @ShellMethod(key = "3", value = "3 - Search contact by phone number")
    public Set<Contact> loadContactByPhoneNumber(@ShellOption(help = "Enter a phone number") String phoneNumber) {
        return contactService.searchContactByPhoneNumber(phoneNumber);
    }

    @ShellMethod(key = "4", value = "4 - Search contact by email")
    public Set<Contact> loadContactByEmail(@ShellOption(help = "Enter an email") String email) {
        return contactService.searchContactByEmail(email);
    }

    @ShellMethod(key = "5", value = "5 - Get all contacts")
    public Set<Contact> getAllContacts() {
        return contactService.getAllContacts();
    }

    @ShellMethod(key = "6", value = "6 - Delete contact")
    public String deleteContact(@ShellOption(help = "Enter an email") String email) {
        contactService.removeContactByEmail(email);
        return "Contact removed successfully";
    }
}

