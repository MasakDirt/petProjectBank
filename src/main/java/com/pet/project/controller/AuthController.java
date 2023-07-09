package com.pet.project.controller;

import com.pet.project.model.dto.auth.LoginRequest;
import com.pet.project.model.dto.auth.TokenResponse;
import com.pet.project.model.dto.customer.CustomerCreateRequest;
import com.pet.project.model.dto.customer.CustomerMapper;
import com.pet.project.model.dto.customer.CustomerResponse;
import com.pet.project.service.CustomerService;
import com.pet.project.service.RoleService;
import com.pet.project.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final CustomerService customerService;
    private final RoleService roleService;
    private final CustomerMapper mapper;

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest request) {
        var userDetails = customerService.loadUserByUsername(request.getUsername());

        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }

        log.info("=== POST-LOGIN/login-post === auth.name = {} === time = {}", userDetails.getUsername(), LocalDateTime.now());
        return new TokenResponse(jwtUtils.generateTokenFromUsername(userDetails.getUsername()));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createNewCustomer(@RequestBody @Valid CustomerCreateRequest signUser) {
        var customer = customerService.create(mapper.createCustomerToCustomer(signUser),
                roleService.readByName("USER"));

        log.info("=== POST-REGISTER/register-post === reg.name = {} === time = {}", customer.getUsername(), LocalDateTime.now());
        return mapper.customerToCustomerResponse(customer);
    }
}
