package com.bidding.platform.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidding.platform.auth.models.User;



public interface UserRepository extends JpaRepository<User , Long>{
    
    public User findByEmail(String email);

}