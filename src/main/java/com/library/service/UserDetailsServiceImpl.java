package com.library.service;

import com.library.model.Customer;
import com.library.model.UserPrincipal;
import com.library.repository.CustomerRepository;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final CustomerRepository customerRepository;

  public UserDetailsServiceImpl(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Customer> optionalCustomer = customerRepository.findByUsername(username);
    if (!optionalCustomer.isPresent()) {
      throw new UsernameNotFoundException("User not found!");
    }
    return new UserPrincipal(optionalCustomer.get());
  }
}
