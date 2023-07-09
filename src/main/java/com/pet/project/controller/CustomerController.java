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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.pet.project.controller.ControllerStaticHelper.getRole;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final RoleService roleService;
    private final CustomerMapper mapper;

    @GetMapping
    @PreAuthorize("@authorizationService.isAdmin(authentication.principal)")
    List<CustomerResponse> getAll(Authentication authentication) {
        var responses = customerService.getAll().stream()
                .map(mapper::customerToCustomerResponse)
                .collect(Collectors.toList());

        log.info("=== GET-CUSTOMERS/admin-get === auth.name = {}", authentication.getPrincipal());
        return responses;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.isUserAdminOrIsUsersSame(authentication.principal, #id)")
    CustomerResponse getOne(@PathVariable long id, Authentication authentication) {
        var user = customerService.readById(id);
        var response = mapper.customerToCustomerResponse(user);

        log.info("=== GET-CUSTOMER/{}-get === auth.name = {}", getRole(user), authentication.getPrincipal());
        return response;
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isAdmin(authentication.principal)")
    @ResponseStatus(HttpStatus.CREATED)
    CustomerResponse createAdmin(@Valid @RequestBody CustomerCreateRequest request, Authentication authentication) {
        var customer = customerService.create(mapper.createCustomerToCustomer(request),
                roleService.readByName("ADMIN"));

        log.info("=== POST-CUSTOMER/admin-post === auth.name = {} === time = {}", authentication.getPrincipal(), LocalDateTime.now());
        return mapper.customerToCustomerResponse(customer);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.isUserAdminOrIsUsersSameForUpdate(authentication.principal, #id, #updateCustomer.id)")
    CustomerResponse update(@PathVariable long id, @RequestBody @Valid CustomerUpdateRequest updateCustomer, Authentication authentication) {
        var user = customerService.readById(id);
        var customer = customerService.update(
                mapper.updateCustomerToCustomer(updateCustomer),
                updateCustomer.getOldPassword()
        );

        log.info("=== PUT-CUSTOMER/{}-put === auth.name = {}", getRole(user), authentication.getPrincipal());
        return mapper.customerToCustomerResponse(customer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationService.isAdmin(authentication.principal)")
    OperationResponse delete(@PathVariable long id, Authentication authentication) {
        var customer = customerService.readById(id);
        customerService.delete(id);

        log.info("=== DELETE-CUSTOMER/admin-delete === auth.name = {}", authentication.getPrincipal());
        return OperationResponse.builder()
                .message("User with name: " + customer.getName() + " has been deleted!")
                .build();
    }


}
