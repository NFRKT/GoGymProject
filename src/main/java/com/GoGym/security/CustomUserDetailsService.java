package com.GoGym.security;

import com.GoGym.Module.user;
import com.GoGym.Service.userService;
import com.GoGym.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final userRepository userRepository;
    private final userService userService;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(@Autowired userRepository userRepository,
                                    @Autowired userService userService,
                                    @Lazy @Autowired PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        user user = userService.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        System.out.println("Loaded user: " + user.getEmail() + ", password: " + user.getPassword());

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();
    }

}

