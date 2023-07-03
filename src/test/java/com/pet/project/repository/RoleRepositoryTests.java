package com.pet.project.repository;

import com.pet.project.model.entity.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

//In Repository layer I check only my methods!
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
public class RoleRepositoryTests {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(roleRepository).isNotNull();
    }

    @Test
    public void checkFindByNameRole() {
        Role expected = new Role();
        expected.setId(6L);
        expected.setName("Experiment");
        roleRepository.save(expected);

        Role actual = roleRepository.findByName("Experiment").orElseThrow(
                () -> new NoSuchElementException("We can not find role with name: " + expected.getName()));

        assertEquals(expected, actual, "Here roles need to be equals");
    }
}