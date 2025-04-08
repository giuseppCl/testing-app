package com.gcl.testingapp.service;

import com.gcl.testingapp.exception.UserNotFoundException;
import com.gcl.testingapp.model.User;
import com.gcl.testingapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User createUser(UserDetails userDetails) {
        User user = new User()
                .setName(userDetails.getUsername());
        return userRepository.save(user);
    }

    public User getUserByName(String userName) {
        return userRepository.findByName(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found:" + userName));
    }
}
