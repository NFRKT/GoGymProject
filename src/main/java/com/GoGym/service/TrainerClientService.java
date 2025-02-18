package com.GoGym.service;

import com.GoGym.module.TrainerClient;
import com.GoGym.module.TrainerDetails;
import com.GoGym.module.TrainerExperience;
import com.GoGym.module.User;
import com.GoGym.repository.TrainerClientRepository;
import com.GoGym.repository.TrainerDetailsRepository;
import com.GoGym.repository.TrainerExperienceRepository;
import com.GoGym.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerClientService {

    private final TrainerClientRepository trainerClientRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public TrainerClientService(TrainerClientRepository trainerClientRepository, UserRepository userRepository, NotificationService notificationService) {
        this.trainerClientRepository = trainerClientRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public TrainerClient createTrainerClient(Long trainerId, Long clientId) {
        TrainerClient trainerClient = new TrainerClient();
        trainerClient.setTrainer(new User(trainerId));
        trainerClient.setClient(new User(clientId));
        return trainerClientRepository.save(trainerClient);
    }

    public List<TrainerClient> getTrainerClients(Long trainerId) {
        return trainerClientRepository.findByTrainer_IdUser(trainerId);
    }

    public List<TrainerClient> getClientTrainers(Long clientId) {
        return trainerClientRepository.findByClient_IdUser(clientId);
    }

    public List<User> getAllTrainers() {
        return userRepository.findAllTrainers();
    }
    public List<User> findAllTrainers() {
        return userRepository.findAllByUserType(User.UserType.TRENER);
    }

    @Transactional
    public void removeTrainerClient(Long trainerId, Long clientId) {
        TrainerClient trainerClient = trainerClientRepository.findByTrainer_IdUserAndClient_IdUser(trainerId, clientId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono relacji trener-klient"));

        User trainer = trainerClient.getTrainer();
        User client = trainerClient.getClient();

        // Usunięcie relacji
        trainerClientRepository.delete(trainerClient);

        // Dodanie powiadomienia dla klienta
        notificationService.createNotification(client, trainer, "trainer_resigned");
    }



}
