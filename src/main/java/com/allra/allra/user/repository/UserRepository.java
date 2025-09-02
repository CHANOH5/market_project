package com.allra.allra.user.repository;


import com.allra.allra.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


} // end class