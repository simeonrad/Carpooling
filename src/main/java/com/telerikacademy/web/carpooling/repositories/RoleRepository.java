package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.Role;

public interface RoleRepository {
    Role findByName(String name);
    Role findById(int id);
}
