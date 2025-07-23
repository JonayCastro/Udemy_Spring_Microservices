package com.zeven.springcloud.msvc.users.repositories;

import com.zeven.springcloud.msvc.users.entities.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
