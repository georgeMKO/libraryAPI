package com.library.security;

import com.library.repository.CustomerRepository;
import com.library.service.UserDetailsServiceImpl;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final CustomerRepository customerRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final UserDetailsServiceImpl userDetailsService;

  public WebSecurityConfig(CustomerRepository customerRepository,
      BCryptPasswordEncoder bCryptPasswordEncoder,
      UserDetailsServiceImpl userDetailsService) {
    this.customerRepository = customerRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers(HttpMethod.POST, "/api/customers/**").permitAll()
        .antMatchers("/h2-console/**").permitAll()
        .anyRequest()
        .authenticated().and()
        .headers().frameOptions().disable()
        .and().cors()
        .and().csrf().disable()
        .addFilter(new JwtAuthenticationFilter(authenticationManager()))
        .addFilter(new JwtAuthorizationFilter(authenticationManager(), customerRepository))
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
  }
}
