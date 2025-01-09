package com.GoGym.Service;
import com.GoGym.Module.UserRegistrationDTO;
import com.GoGym.Module.user;
import com.GoGym.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
@ComponentScan({ "com.GoGym.repository.userRepository" })
public class userService {

    @Autowired
    private userRepository UserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    public userService(userRepository UserRepository) {
        this.UserRepository = UserRepository;
    }
    public List<user> hi(){
        List<user> users = UserRepository.findAll();
        System.out.println("Nurses: " + users); // Loguj dane
        return users;
    }
    public Optional<user> getFirstUser() {
        List<user> users = UserRepository.findAll();
        if (!users.isEmpty()) {
            return Optional.of(users.get(0));  // Zwracamy pierwszą pielęgniarkę
        }
        return Optional.empty();
    }

    public user registerUser(UserRegistrationDTO userDTO) {
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        user newUser = new user(
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

   // public void saveUser(user user) {
   //     UserRepository.save(user);
   // }

    public user findByEmail(String email) {
        return UserRepository.findByEmail(email);
    }
}
