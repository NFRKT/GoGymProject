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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Wczytaj użytkownika z bazy danych
        com.GoGym.Module.user user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Utwórz UserDetails z zakodowanym hasłem

        System.out.println("Loaded user: " + user.getEmail() + ", password: " + user.getPassword());
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // Hasło zakodowane w BCrypt
                .roles(user.getUserType().name()) // Rola użytkownika
                .build();
    }

}

