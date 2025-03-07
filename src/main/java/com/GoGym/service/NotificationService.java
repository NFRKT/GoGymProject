package com.GoGym.service;

import com.GoGym.module.Notification;
import com.GoGym.module.User;
import com.GoGym.repository.NotificationRepository;
import com.GoGym.repository.TrainerClientRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final TrainerClientRepository trainerClientRepository;

    public void createNotification(User client, User trainer, String status) {
        createNotification(client, trainer, status, null);
    }

    public void createNotification(User client, User trainer, String status, String additionalInfo) {
        String message;
        boolean isStillWorkingTogether = trainerClientRepository.existsByTrainerAndClient(client, trainer);
        if (!isStillWorkingTogether && (status.equals("day_updated") || status.equals("plan_completed") || status.equals("rest_day_completed"))) {
//            log.info("Pominięto powiadomienie dla trenera {} od klienta {}, ponieważ współpraca została zakończona.", trainer.getIdUser(), client.getIdUser());
            return;
        }

        switch (status) {
            case "accepted":
                message = "Twoja prośba o współpracę z trenerem " + trainer.getFirstName() + " " + trainer.getLastName() + " została zaakceptowana!";
                break;
            case "rejected":
                message = "Twoja prośba o współpracę z trenerem " + trainer.getFirstName() + " " + trainer.getLastName() + " została odrzucona.";
                break;
            case "trainer_resigned":
                message = "Trener " + trainer.getFirstName() + " " + trainer.getLastName() + " zakończył z Tobą współpracę.";
                break;
            case "new_plan":
                message = "Twój trener " + trainer.getFirstName() + " " + trainer.getLastName() + " stworzył dla Ciebie nowy plan: " + additionalInfo + ".";
                break;
            case "updated_plan":
                message = "Twój trener " + trainer.getFirstName() + " " + trainer.getLastName() + " edytował Twój plan: " + additionalInfo + ".";
                break;
            case "new_request":
                message = "Nowa prośba o współpracę od " + trainer.getFirstName() + " " + trainer.getLastName() + ".";
                break;
            case "day_updated":
                message = "Twój klient " + trainer.getFirstName() + " " + trainer.getLastName() + " ukończył dzień treningowy: " + additionalInfo  + ".";
                break;
            case "plan_completed":
                message = "Twój klient " + trainer.getFirstName() + " " + trainer.getLastName() + " ukończył plan treningowy: " + additionalInfo + ".";
                break;
            case "rest_day_completed":
                message = "Twój klient " + trainer.getFirstName() + " " + trainer.getLastName() + " ukończył dzień regeneracyjny: " + additionalInfo + ".";
                break;
            case "badge_awarded":
                message = "Gratulacje! Otrzymałeś odznakę: " + additionalInfo + ".";
                break;
            default:
                throw new IllegalArgumentException("Nieznany status powiadomienia: " + status);
        }

        Notification notification = new Notification(client, message);
        notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono powiadomienia"));
        notification.setStatus(Notification.NotificationStatus.READ);
        notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
