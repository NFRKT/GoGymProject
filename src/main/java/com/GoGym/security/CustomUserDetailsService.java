package com.GoGym.security;

import com.GoGym.Module.user;
import com.GoGym.Service.userService;
import com.GoGym.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final userRepository userRepository;
    private final userService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailsService(userRepository userRepository,
                                    userService userService,
                                    @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Wczytaj użytkownika z bazy danych
        com.GoGym.Module.user user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Zwróć instancję CustomUserDetails z klasą user
        return new CustomUserDetails(user);
    }
    }



