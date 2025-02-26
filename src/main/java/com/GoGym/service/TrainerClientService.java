package com.GoGym.service;
import com.GoGym.module.ChatRoom;
import com.GoGym.module.TrainerClient;
import com.GoGym.module.User;
import com.GoGym.repository.ChatRoomRepository;
import com.GoGym.repository.TrainerClientRepository;
import com.GoGym.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TrainerClientService {

    @Autowired
    private final TrainerClientRepository trainerClientRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final ChatRoomRepository chatRoomRepository;

    public TrainerClient createTrainerClient(Long trainerId, Long clientId) {
        TrainerClient trainerClient = new TrainerClient();
        trainerClient.setTrainer(new User(trainerId));
        trainerClient.setClient(new User(clientId));

        TrainerClient savedRelation = trainerClientRepository.save(trainerClient);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUser(trainerClient.getClient());
        chatRoom.setTrainer(trainerClient.getTrainer());
        chatRoomRepository.save(chatRoom);

        return savedRelation;
    }

    public List<TrainerClient> getTrainerClients(Long trainerId) {
        return trainerClientRepository.findByTrainer_IdUser(trainerId);
    }

    public List<TrainerClient> getClientTrainers(Long clientId) {
        return trainerClientRepository.findByClient_IdUser(clientId);
    }

    public List<User> findAllTrainers() {
        return userRepository.findAllByUserType(User.UserType.TRAINER);
    }

    @Transactional
    public void removeTrainerClient(Long trainerId, Long clientId) {
        Optional<TrainerClient> trainerClientOpt = trainerClientRepository.findByTrainer_IdUserAndClient_IdUser(trainerId, clientId);
        if (trainerClientOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Relacja trener-klient nie istnieje");
        }
        TrainerClient trainerClient = trainerClientOpt.get();

        // Usunięcie pokoju chatu
        chatRoomRepository.deleteByUserAndTrainer(trainerClient.getClient(), trainerClient.getTrainer());

        trainerClientRepository.delete(trainerClient);
        notificationService.createNotification(trainerClient.getClient(), trainerClient.getTrainer(), "trainer_resigned");
    }
}
