package com.pet.project.service;

import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class RoleServiceTests {

    private final RoleService roleService;
    private List<Role> roles;

    @Autowired
    public RoleServiceTests(RoleService roleService) {
        this.roleService = roleService;
    }

    @BeforeEach
    public void init() {
        roles = roleService.getAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(roleService).isNotNull();
    }

    @Test
    public void checkGetAll() {
        assertAll(
                () -> assertTrue(roleService.getAll().size() > 0,
                        "There must be more than 0 roles."),
                () ->  assertEquals(roles, roleService.getAll(),
                        "Roles and roleService.getAll() must be equal")
        );
    }

    @Test
    public void checkCreateMethod() {
        Role role = new Role();
        role.setName("NewRole");
        roleService.create(role);

        assertAll(
                () -> assertTrue(roles.size() < roleService.getAll().size(),
                        "Roles size must be smaller, because we create new role"),

                () -> assertNotEquals(roles, roleService.getAll(),
                        "Roles must not be equal to all roles after creating")
        );
    }

    @Test
    public void checkNotValidCreateMethod() {
        assertAll(
                () -> assertThrows(NullEntityReferenceException.class, () -> roleService.create(null),
                        "There need to be NullEntityReferenceException because we are pass null."),

                () -> assertThrows(ConstraintViolationException.class, () -> roleService.create(new Role()),
                        "There need to be ConstraintViolationException because we are pass object without tabular values.")
        );
    }

    @Test
    public void checkReadByIdRole() {
        Role expected = new Role();
        expected.setId(5L);
        expected.setName("newRole");
        roleService.create(expected);

        assertEquals(expected, roleService.readById(expected.getId()),
                "Roles need to be equals, if it isn`t, please check rolesId");
    }

    @Test
    public void checkInvalidReadByIdRole() {
        assertThrows(EntityNotFoundException.class, () -> roleService.readById(10000L),
                "There must be EntityNotFoundException because we have not role with id 10000");
    }

    @Test
    public void checkUpdateRole() {
        Role expected = new Role();
        expected.setId(2L);
        expected.setName("updateRole");
        roleService.update(expected);

        assertEquals(expected, roleService.readById(expected.getId()),
                "There must be equal roles after updating one!");
    }

    @Test
    public void checkInvalidUpdateRole() {
        Role invalidRole = new Role();
        invalidRole.setId(1000000L);

        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> roleService.update(invalidRole),
                        "There should be EntityNotFoundException because in DB we haven`t role with this id!"),

                () -> assertThrows(NullPointerException.class, () -> roleService.update(new Role()),
                        "There should be NullPointerException because we need that role name will be not a null!"),

                () -> assertThrows(NullEntityReferenceException.class, () -> roleService.update(null),
                        "There should be NullEntityReferenceException because we don`t need null in update method.")
        );
    }

    @Test
    public void checkDeleteRole() {
        roleService.delete(2L);

        assertTrue(roles.size() > roleService.getAll().size(),
                "Roles size must be smaller than roleService.getAll() size after deleting role");
    }

    @Test
    public void checkReadByNameRole() {
        Role expected = new Role();
        expected.setId(4L);
        expected.setName("RoleForReading");

        roleService.create(expected);

        Role actual = roleService.readByName("RoleForReading");
        assertEquals(expected, actual,
                "They must be equal, if it isn`t, please check rolesId");
    }

    @Test
    public void checkInvalidReadByNameRole() {
        assertAll(
                () ->  assertThrows(EntityNotFoundException.class, () -> roleService.readByName("invalidName"),
                        "There should be EntityNotFoundException because in DB we haven`t role with this name!"),
                () ->  assertThrows(NullEntityReferenceException.class, () -> roleService.readByName(null),
                        "There should be NullEntityReferenceException because we don`t need null in readByName method.")
        );

    }
}
