package com.openrangelabs.services.operations.jwt;

import com.openrangelabs.services.operations.model.User;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService {
    public User loadUserByUsername(String username) {
        User u = new User();
        u.setUsername(username);
        u.setId(1l);
        return u;
    }
}
