package com.library.controller;

import com.library.model.Customer;
import com.library.repository.CustomerRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerRepository customerRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @GetMapping("/customers")
  public ResponseEntity<List<Customer>> getAllCustomers() {
    List<Customer> customers = new ArrayList<>(customerRepository.findAll());

    if (customers.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return ResponseEntity.ok(customers);
  }

  @GetMapping("/customers/{id}")
  public ResponseEntity<Customer> getCustomerById(@PathVariable("id") long id) {
    Optional<Customer> optionalCustomer = customerRepository.findById(id);
    return ResponseEntity.of(optionalCustomer);
  }

  @PostMapping("/customers")
  public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
    Customer persistedCustomer = customerRepository
        .save(Customer.builder().username(customer.getUsername())
            .password(bCryptPasswordEncoder.encode(customer.getPassword())).build());
    return ResponseEntity.status(HttpStatus.CREATED).body(persistedCustomer);
  }

  @PutMapping("/customers/{id}")
  public ResponseEntity<Customer> updateCustomer(@PathVariable("id") long id,
      @RequestBody Customer customer) {
    Optional<Customer> optionalCustomer = customerRepository.findById(id);
    if (optionalCustomer.isPresent()) {
      Customer updateCustomer = optionalCustomer.get();
      updateCustomer.setUsername(customer.getUsername());
      if(!bCryptPasswordEncoder.matches(customer.getPassword(), updateCustomer.getPassword())) {
        updateCustomer.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()));
      }
      return ResponseEntity.ok(customerRepository.save(updateCustomer));
    } else {
      return ResponseEntity.noContent().build();
    }
  }

  @DeleteMapping("/customers/{id}")
  public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable("id") long id) {
    customerRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/customers")
  public ResponseEntity<HttpStatus> deleteAllCustomers() {
    customerRepository.deleteAll();
    return ResponseEntity.noContent().build();
  }

}
