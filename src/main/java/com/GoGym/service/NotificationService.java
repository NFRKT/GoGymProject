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
        String message = "Twoja prośba o współpracę z trenerem " + trainer.getFirstName() + " " + trainer.getSecondName() +
                " została " + (status.equals("accepted") ? "zaakceptowana!" : "odrzucona.");

        Notification notification = new Notification(client, message);
        notificationRepository.save(notification);
    }


    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndStatus(user, Notification.NotificationStatus.UNREAD);
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono powiadomienia"));
        notification.setStatus(Notification.NotificationStatus.READ);
        notificationRepository.save(notification);
    }

    public void markAsReadByMessage(String message) {
        Notification notification = notificationRepository.findByMessage(message)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono powiadomienia"));
        notification.setStatus(Notification.NotificationStatus.READ);
        notificationRepository.save(notification);
    }

}
