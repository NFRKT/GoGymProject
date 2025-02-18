package com.GoGym.service;

import com.GoGym.module.Notification;
import com.GoGym.module.User;
import com.GoGym.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    public void createNotification(User client, User trainer, String status) {
        createNotification(client, trainer, status, null); // Przekazujemy `null` jako domyślną nazwę planu
    }
    public void createNotification(User client, User trainer, String status, String planName) {
        String message;

        switch (status) {
            case "accepted":
                message = "Twoja prośba o współpracę z trenerem " + trainer.getFirstName() + " " + trainer.getSecondName() + " została zaakceptowana!";
                break;
            case "rejected":
                message = "Twoja prośba o współpracę z trenerem " + trainer.getFirstName() + " " + trainer.getSecondName() + " została odrzucona.";
                break;
            case "trainer_resigned":
                message = "Trener " + trainer.getFirstName() + " " + trainer.getSecondName() + " zakończył z Tobą współpracę.";
                break;
            case "new_plan":
                message = "Twój trener " + trainer.getFirstName() + " " + trainer.getSecondName() + " stworzył dla Ciebie nowy plan: " + planName + ".";
                break;
            case "updated_plan":
                message = "Twój trener " + trainer.getFirstName() + " " + trainer.getSecondName() + " edytował Twój plan: " + planName + ".";
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


    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono powiadomienia"));
        notification.setStatus(Notification.NotificationStatus.READ);
        notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }


}
