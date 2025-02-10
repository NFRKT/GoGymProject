package com.GoGym.service;
import com.GoGym.module.TrainerSpecialization;
import com.GoGym.module.User;
import com.GoGym.module.TrainerDetails;
import com.GoGym.dto.UserRegistrationDTO;
import com.GoGym.repository.TrainerSpecializationRepository;
import com.GoGym.repository.UserRepository;
import com.GoGym.repository.TrainerDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@ComponentScan({ "com.GoGym.repository.UserRepository" })
public class UserService {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainerDetailsRepository trainerDetailsRepository;
    @Autowired
    private TrainerSpecializationRepository specializationRepository;


    public User registerUser(UserRegistrationDTO userDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalStateException("Użytkownik z podanym adresem e-mail już istnieje.");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        User.UserType userTypeEnum = User.UserType.valueOf(userDTO.getUserType().toUpperCase());
        User.Gender genderEnum = User.Gender.valueOf(userDTO.getGender().toUpperCase());

        User newUser = new User(null, userDTO.getEmail(), encodedPassword, userDTO.getFirstName(),
                userDTO.getSecondName(), userDTO.getBirthDate(), genderEnum, userTypeEnum);

        User savedUser = userRepository.save(newUser);

        if (userTypeEnum == User.UserType.TRENER) {
            TrainerDetails trainerDetails = new TrainerDetails();
            trainerDetails.setIdTrainer(Math.toIntExact(savedUser.getIdUser()));
            trainerDetails.setStartDate(userDTO.getStartDate());
            trainerDetails.setPhoneNumber(userDTO.getPhoneNumber());
            trainerDetails.setWorkArea(userDTO.getWorkArea());

            TrainerDetails savedTrainer = trainerDetailsRepository.save(trainerDetails);

            if (userDTO.getSpecializations() != null) {
                for (String specialization : userDTO.getSpecializations()) {
                    TrainerSpecialization spec = new TrainerSpecialization();
                    spec.setTrainer(savedTrainer);
                    spec.setSpecialization(specialization);
                    specializationRepository.save(spec);
                }
            }

        }

        return savedUser;
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Nie znaleziono użytkownika o podanym adresie email: " + email));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Nie znaleziono użytkownika"));
    }



}
