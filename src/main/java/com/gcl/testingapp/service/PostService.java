package com.gcl.testingapp.service;

import org.springframework.stereotype.Service;

@Service
public class PostService {
    public String create(String content) {
        return "Created post: " + content;
    }
}
