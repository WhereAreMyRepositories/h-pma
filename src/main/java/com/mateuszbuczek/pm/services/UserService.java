package com.mateuszbuczek.pm.services;

import com.mateuszbuczek.pm.domain.User;
import com.mateuszbuczek.pm.exceptions.UsernameAlreadyExistsException;
import com.mateuszbuczek.pm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser (User newUser) {
        // username has to be unique (exception)
        try {
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            // we don't persist or show the confirm password
//            newUser.setConfirmPassword("");
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new UsernameAlreadyExistsException("Username '" + newUser.getUsername() + "' already exists.");
        }
    }
}
