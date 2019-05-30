package com.example.login;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface UserRepository extends CrudRepository<Users, Integer> {

    @Query(value = "SELECT * FROM users WHERE email = ?1 and password = ?2", nativeQuery = true)
    Iterable<Users> findByEmailAndPassword(String email, String password);

}