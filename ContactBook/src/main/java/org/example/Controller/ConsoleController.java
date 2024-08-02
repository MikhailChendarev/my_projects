package org.example.Controller;

import org.example.model.Contact;
import org.example.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Scanner;
import java.util.Set;

@ShellComponent
public class ConsoleController {

    private final String enterName = "Enter a name";

    private final String enterPhoneNumber = "Enter a phone number";

    private final String enterEmail = "Enter a email";

    private Scanner scanner = new Scanner(System.in);

    @Autowired
    private ContactService contactService;

    @ShellMethod(key = "1", value = "1 - Add a new contact")
    public String addContact() {
        System.out.println(enterName);
        String name = scanner.nextLine();
        System.out.println(enterPhoneNumber);
        String phoneNumber = scanner.nextLine();
        System.out.println(enterEmail);
        String email = scanner.nextLine();
        Contact contact = new Contact(name, phoneNumber, email);
        contactService.addContact(contact);
        return "Contact added successfully";
    }

    @ShellMethod(key = "2", value = "2 - Search contact by name")
    public Set<Contact> loadContactByName() {
        System.out.println(enterName);
        return contactService.searchContactByName(scanner.nextLine());
    }

    @ShellMethod(key = "3", value = "3 - Search contact by phone number")
    public Set<Contact> loadContactByPhoneNumber() {
        System.out.println(enterPhoneNumber);
        return contactService.searchContactByPhoneNumber(scanner.nextLine());
    }

    @ShellMethod(key = "4", value = "4 - Search contact by email")
    public Set<Contact> loadContactByEmail() {
        System.out.println(enterEmail);
        return contactService.searchContactByEmail(scanner.nextLine());
    }

    @ShellMethod(key = "5", value = "5 - Get all contacts")
    public Set<Contact> getAllContacts() {
        return contactService.getAllContacts();
    }

    @ShellMethod(key = "6", value = "6 - Delete contact")
    public String deleteContact() {
        contactService.removeContactByEmail(scanner.nextLine());
        return "Contact removed successfully";
    }
}
