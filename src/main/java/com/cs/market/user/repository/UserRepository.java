package com.cs.market.user.repository;


import com.cs.market.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


} // end class