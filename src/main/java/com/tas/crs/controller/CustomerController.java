package com.tas.crs.controller;

import com.tas.crs.dto.EmailDto;
import com.tas.crs.entity.Customer;
import com.tas.crs.exception.CustomerNotFoundException;
import com.tas.crs.service.CustomerServiceImpl;
import com.tas.crs.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/api/v1/customers")
public class CustomerController {

    private final CustomerServiceImpl mCustomerService;
    private final EmailService mEmailService;

    @Autowired
    public CustomerController(CustomerServiceImpl customerService, EmailService emailService) {
        mCustomerService = customerService;
        mEmailService = emailService;
    }

    @PostMapping
    public ResponseEntity<Customer> createAccount(final @RequestBody Customer customer) {
        return new ResponseEntity<>(mCustomerService.addCustomer(customer), CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getCustomers() {
        return new ResponseEntity<>(mCustomerService.fetchCustomers(), OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Customer> getCustomer(final @PathVariable(name = "id") Long customerId) {
        Customer customer = mCustomerService
                .fetchCustomer(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Customer with ID: %s not found", customerId)
                ));
        return ResponseEntity.badRequest().body(customer);
    }

    @PostMapping(path = {"/email"})
    public ResponseEntity<?> sendEmailNotification(final @RequestBody EmailDto emailDto) {
        this.mEmailService.send(
                emailDto.getDestination(),
//                emailDto.getMessageContent()
                "<html>" +
                        "<head>" +
                        "<title>Customer Registration</title>" +
                        "<style>" +
                        "h1{background-color: #d6d2b2; color: white;}" +
                        "body{padding: 10px;}" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<h1>Confirm Registration</h1>" +
                        "Dear, valuable customer<br/><br/>" +
                        "Welcome,<br/> please fill the form below to end your registration process<br/><br/>" +
                        "<div style=\"display: \"flex\"; flex-direction: \"column\"; justify-content: \"center\"; \">" +
                        "<form>" +
                        "<di><label for=\"first_name\">First name</label>" +
                        "<br />" +
                        "<input type=\"text\" id=\"first_name\" />" +
                        "</div>" +
                        "<di><label for=\"last_name\">Last name</label>" +
                        "<br />" +
                        "<input type=\"text\" id=\"last_name\" />" +
                        "</div>" +
                        "<br />" +
                        "<input type=\"submit\" />" +
                        "</form>" +
                        "</div>" +
                        "<br />" +
                        "<br />" +
                        "<a href=\"http://localhost:8080/api/v1/customers/confirm?token=yes\">Activation Link</a><br/><br/>" +
                        "This link will expires in 15 min." +
                        "</body>" +
                        "</html>");
        return ResponseEntity.ok(String.format("Email sent to %s", emailDto.getDestination()));
    }
}
