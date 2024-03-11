package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.Role;

import java.util.List;

public interface RoleRepository {
    Role findByName(String name);
    Role findById(int id);
    List<Role> getAll();
}
