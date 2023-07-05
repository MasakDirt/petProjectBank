package com.pet.project.controller;

import com.pet.project.model.dto.customer.CustomerCreateRequest;
import com.pet.project.model.dto.customer.CustomerMapper;
import com.pet.project.model.dto.customer.CustomerResponse;
import com.pet.project.model.dto.customer.CustomerUpdateRequest;
import com.pet.project.model.dto.utils.OperationResponse;
import com.pet.project.service.CustomerService;
import com.pet.project.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final RoleService roleService;
    private final CustomerMapper mapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    List<CustomerResponse> getAll() {
        var responses = customerService.getAll().stream()
                .map(mapper::customerToCustomerResponse)
                .collect(Collectors.toList());
        log.info("Admin checks all users");
        return responses;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or " +
            "@authorizationService.checkIfUsersSame(#id)")
    CustomerResponse getOne(@PathVariable long id) {
        var response = mapper.customerToCustomerResponse(customerService.readById(id));
        log.info("Customer with id {} was read", id);
        return response;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    CustomerResponse createAdmin(@Valid @RequestBody CustomerCreateRequest request) {
        var customer = customerService.create(mapper.customerCreateToCustomer(request),
                roleService.readByName("ADMIN"));
        log.info("New customer account successfully create: {}", LocalDateTime.now());
        return mapper.customerToCustomerResponse(customer);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    CustomerResponse update(@PathVariable long id, @RequestBody @Valid CustomerUpdateRequest updateCustomer) {
        var customer = customerService.update(
                mapper.updateCustomerToCustomer(updateCustomer),
                updateCustomer.getOldPassword()
        );
        log.info("User with id {} successfully updated", id);
        return mapper.customerToCustomerResponse(customer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or " +
            "@authorizationService.checkIfUsersSame(#id)")
    OperationResponse delete(@PathVariable long id) {
        customerService.delete(id);
        log.info("User with id {} was delete", id);
        return new OperationResponse("User with id: " + id + " has been deleted!");
    }
}
