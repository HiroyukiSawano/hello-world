package org.example.myjweb.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GreetingService {

    public String greet(String name) {
        if (!StringUtils.hasText(name)) {
            return "Hello";
        }
        return "Hello, " + name.trim();
    }
}
