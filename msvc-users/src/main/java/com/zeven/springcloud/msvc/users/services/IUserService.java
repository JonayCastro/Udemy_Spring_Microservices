package com.zeven.springcloud.msvc.users.services;


import com.zeven.springcloud.msvc.users.entities.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    Optional<User> findById(Long id);
    Optional<User> findByUserName(String userName);
    Iterable<User> findAll();
    User save(User user);
    Optional<User> update(User user, Long id);
    void delete(Long id);
}
