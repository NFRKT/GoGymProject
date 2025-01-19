package com.GoGym.service;
import com.GoGym.module.User;
import com.GoGym.dto.UserRegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
@ComponentScan({ "com.GoGym.repository.UserRepository" })
public class UserService {

    @Autowired
    private com.GoGym.repository.UserRepository UserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    public UserService(com.GoGym.repository.UserRepository UserRepository) {
        this.UserRepository = UserRepository;
    }
    public Optional<User> getFirstUser() {
        List<User> users = UserRepository.findAll();
        if (!users.isEmpty()) {
            return Optional.of(users.get(0));  // Zwracamy pierwszą pielęgniarkę
        }
        return Optional.empty();
    }

    public User registerUser(UserRegistrationDTO userDTO) {
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        User newUser = new User(
                null,
                userDTO.getEmail(),
                encodedPassword,
                userDTO.getFirstName(),
                userDTO.getSecondName(),
                userDTO.getBirthDate(),
                userDTO.getGender(),
                userDTO.getUserType()

        );

        return UserRepository.save(newUser);
    }

   // public void saveUser(User User) {
   //     UserRepository.save(User);
   // }

    public User findByEmail(String email) {
        return UserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Nie znaleziono użytkownika o podanym adresie email: " + email));
    }
}
