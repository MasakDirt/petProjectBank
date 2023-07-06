package com.pet.project.model.dto.customer;

import com.pet.project.model.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer createCustomerToCustomer(CustomerCreateRequest customerCreateRequest);
    @Mapping(target = "role", expression = "java(customer.getRole().getName())")
    CustomerResponse customerToCustomerResponse(Customer customer);

    @Mapping(target = "password", source = "newPassword")
    Customer updateCustomerToCustomer(CustomerUpdateRequest request);
}
