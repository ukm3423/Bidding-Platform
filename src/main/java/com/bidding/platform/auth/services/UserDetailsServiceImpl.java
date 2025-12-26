package com.bidding.platform.auth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.UserRepository;


@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepo.findByEmail(username)
        		.orElseThrow(() -> new RuntimeException("USER_NOT_REGISTERED"));

    }

    public UserDetails save(User user) {
        userRepo.save(user);
        return loadUserByUsername(user.getUsername());
    }
}