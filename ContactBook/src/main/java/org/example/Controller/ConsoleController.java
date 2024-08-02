package org.example.Controller;

import org.example.model.Contact;
import org.example.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Set;

@ShellComponent
public class ConsoleController {

    private final String enterName = "Enter a name";

    private final String enterPhoneNumber = "Enter a phone number";

    private final String enterEmail = "Enter a email";

    @Autowired
    private ContactService contactService;

    @ShellMethod(key = "1", value = "1 - Add a new contact")
    public String addContact(@ShellOption(enterName) String name,
                             @ShellOption(enterPhoneNumber) String phoneNumber,
                             @ShellOption(enterEmail) String email) {
        Contact contact = new Contact(name, phoneNumber, email);
        contactService.addContact(contact);
        return "Contact added successfully";
    }

    @ShellMethod(key = "2", value = "2 - Search contact by name")
    public Set<Contact> loadContactByName(@ShellOption(enterName) String name) {
        return contactService.searchContactByName(name);
    }

    @ShellMethod(key = "3", value = "3 - Search contact by phone number")
    public Set<Contact> loadContactByPhoneNumber(@ShellOption(enterPhoneNumber) String phoneNumber) {
        return contactService.searchContactByPhoneNumber(phoneNumber);
    }

    @ShellMethod(key = "4", value = "4 - Search contact by email")
    public Set<Contact> loadContactByEmail(@ShellOption(enterEmail) String email) {
        return contactService.searchContactByEmail(email);
    }

    @ShellMethod(key = "5", value = "5 - Get all contacts")
    public  Set<Contact> getAllContacts() {
        return contactService.getAllContacts();
    }

    @ShellMethod(key = "6", value = "6 - Delete contact")
    public String deleteContact(@ShellOption(enterEmail) String email) {
        Set<Contact> contacts = contactService.searchContactByEmail(email);
        if (contacts.isEmpty()) {
            return "No contact found with the given email.";
        } else {
            Contact contact = contacts.iterator().next();
            contactService.removeContact(contact);
            return "Contact removed successfully.";
        }
    }
}
