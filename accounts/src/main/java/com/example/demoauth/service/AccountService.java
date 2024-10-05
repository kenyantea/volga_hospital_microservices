package com.example.demoauth.service;

import com.example.demoauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    UserRepository userRepository;

}
