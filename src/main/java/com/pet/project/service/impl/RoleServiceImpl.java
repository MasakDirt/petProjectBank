package com.pet.project.service.impl;

import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.entity.Role;
import com.pet.project.repository.RoleRepository;
import com.pet.project.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role create(Role role) {
        try {
            return roleRepository.save(role);
        } catch (InvalidDataAccessApiUsageException exception) {
            throw new NullEntityReferenceException("Role cannot be 'null'");
        }
    }

    @Override
    public void delete(long id) {
        Role role = readById(id);
        roleRepository.delete(role);
    }

    @Override
    public Role update(Role role) {
        if (role != null) {
            readById(role.getId());
            return roleRepository.save(role);
        }
        throw new NullEntityReferenceException("Role cannot be 'null'");
    }

    @Override
    public Role readById(long id) {
        return roleRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Role with id %d not found", id)));
    }

    @Override
    public Role readByName(String name) {
        if (name != null) {
            return roleRepository.findByName(name).orElseThrow(() ->
                    new EntityNotFoundException(String.format("Role with name %s not found", name)));
        }
        throw new NullEntityReferenceException("Name cannot be 'null'");
    }

    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }
}
