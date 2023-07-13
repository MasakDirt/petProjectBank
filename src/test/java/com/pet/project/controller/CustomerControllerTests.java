package com.pet.project.controller;

import com.pet.project.model.dto.auth.LoginRequest;
import com.pet.project.model.dto.customer.CustomerMapper;
import com.pet.project.model.dto.customer.CustomerUpdateRequest;
import com.pet.project.model.entity.Customer;
import com.pet.project.service.CustomerService;
import com.pet.project.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.pet.project.controller.ControllerTestsStaticHelper.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CustomerControllerTests {
    private final MockMvc mvc;
    private final CustomerService customerService;
    private final RoleService roleService;
    private final CustomerMapper mapper;
    private static final String BASIC_URL = "/api/customers";
    private String token;

    @Autowired
    public CustomerControllerTests(MockMvc mvc, CustomerService customerService,
                                   CustomerMapper customerMapper, RoleService roleService) {
        this.mvc = mvc;
        this.customerService = customerService;
        this.mapper = customerMapper;
        this.roleService = roleService;
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(mvc).isNotNull();
        assertThat(customerService).isNotNull();
        assertThat(mapper).isNotNull();
        assertThat(roleService).isNotNull();
    }

    @BeforeEach
    void init() throws Exception {
        // ADMIN
        token = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new LoginRequest("mike@mail.co", "1111"))
                        )
                )
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void test_Valid_GetAll() throws Exception {
        var expected = asJsonString(customerService.getAll().stream()
                .map(mapper::customerToCustomerResponse)
                .toList());

        mvc.perform(get(BASIC_URL)
                        .header("Authorization", "Bearer " + token)
                   )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(expected, result.getResponse().getContentAsString(),
                                "Here, we need to get all customers that we have in our db!")
                );
    }

    @Test
    public void test_Invalid_UserRole_GetAll() throws Exception {
        // USER
        token = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(getLoginRequest("mila@mail.co", "3333"))
                        )
                )
                .andReturn().getResponse().getContentAsString();

        mvc.perform(get(BASIC_URL)
                        .header("Authorization", "Bearer " + token)
                        )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse().getContentAsString(),
                                "Here must be a response about problem!")
                );
    }

    @Test
    public void test_Valid_GetCustomer() throws Exception {
        long id = 2L;
        var expected = asJsonString(mapper.customerToCustomerResponse(customerService.readById(id)));

        mvc.perform(get(BASIC_URL + "/{id}", id)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(expected, result.getResponse().getContentAsString(),
                                "Here, we need to get customer with id 2 like a CustomerResponse obj!")
                );
    }

    @Test
    public void test_Valid_CreateCustomerAdmin() throws Exception {
        var createRequest = getCustomerCreateRequest("Maks", "Boss", "boss@mail.com", "forJobAcc");
        var expected = mapper.createCustomerToCustomer(createRequest);
        expected.setId(4L);
        expected.setRole(roleService.readByName("ADMIN"));
        int amountOfCustomersBefore = customerService.getAll().size();

        mvc.perform(post(BASIC_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertEquals(asJsonString(mapper.customerToCustomerResponse(expected)), result.getResponse().getContentAsString(),
                                "Here must be equal customers."));

        assertTrue(amountOfCustomersBefore < customerService.getAll().size(),
                "When customer created, size of all customers need to be bigger than before!");
    }

    @Test
    public void test_Valid_UpdateCustomer() throws Exception {
        long id = 2L;
        var update = new CustomerUpdateRequest(id, "Maks", "Cool", "2222", "9870");
        var customer = mapper.updateCustomerToCustomer(update);
        customer.setRole(roleService.readByName("USER"));
        customer.setEmail(customerService.readById(2L).getEmail());

        mvc.perform(put(BASIC_URL + "/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(update))
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(mapper.customerToCustomerResponse(customer)),
                                result.getResponse().getContentAsString(),
                                "Customers must be equal after updating!")
                        );
    }

    @Test
    public void test_Invalid_NotMatchesPassword_UpdateCustomer() throws Exception {
        long id = 2L;
        var update = new CustomerUpdateRequest(id, "Maks", "Cool", "invalid_pass", "9870");

        mvc.perform(put(BASIC_URL + "/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(update))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse().getContentAsString(),
                                "Here must be a response about problem!")
                );
    }

    @Test
    public void test_Invalid_NotMatchesId_UpdateCustomer() throws Exception {
        var update = new CustomerUpdateRequest(2L, "Maks", "Cool", "invalid_pass", "9870");

        mvc.perform(put(BASIC_URL + "/{id}", 1L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(update))
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse().getContentAsString(),
                                "Here must be a response about problem!")
                );
    }

    @Test
    public void test_Valid_DeleteCustomer() throws Exception {
        Customer customer = new Customer();
        customer.setFirstName("Vlad");
        customer.setLastName("Bob");
        customer.setEmail("bob@mail.co");
        customer.setPassword("newpass");
        var expected = customerService.create(customer, roleService.readByName("USER"));

        int customersBeforeDeleting = customerService.getAll().size();

        mvc.perform(delete(BASIC_URL + "/{id}", expected.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals("{\"message\":\"User with name: " + expected.getName() + " has been deleted!\"}",
                                result.getResponse().getContentAsString(),
                                "It`s messages must be equal!")
                );

        assertTrue(customersBeforeDeleting > customerService.getAll().size(),
                "When customer deleted, size of all customers need to be smaller than before!");
    }

    @Test
    public void test_Invalid_CustomerId_DeleteCustomer() throws Exception {
        mvc.perform(delete(BASIC_URL + "/{id}", 100L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse().getContentAsString(),
                                "Here must be a response about problem!")
                        );
    }
}