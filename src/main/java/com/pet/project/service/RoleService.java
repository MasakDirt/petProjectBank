package com.pet.project.service;

import com.pet.project.model.Role;

import java.util.List;

public interface RoleService {
    Role create(Role role);
    void delete(long id);
    Role update(Role role);
    Role readById(long id);
    Role readByName(String name);
    List<Role> getAll();
}
