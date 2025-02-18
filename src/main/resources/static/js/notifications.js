document.addEventListener("DOMContentLoaded", function () {
    const bellButton = document.getElementById("notifications-bell");
    const dropdown = document.getElementById("notifications-dropdown");
    const notificationsList = document.getElementById("notifications-list");

    if (!bellButton || !dropdown || !notificationsList) {
        console.error("Brak elementów powiadomień w DOM.");
        return;
    }

function fetchNotifications() {
    fetch("/notifications/all") // Pobieramy WSZYSTKIE powiadomienia, nie tylko nieodczytane
        .then(response => response.json())
        .then(data => {
            notificationsList.innerHTML = "";

            if (data.length === 0) {
                notificationsList.innerHTML = "<li>Brak powiadomień</li>";
                bellButton.classList.remove("new-notifications");
                return;
            }

            data.forEach(notification => {
                let listItem = document.createElement("li");
                listItem.dataset.id = notification.id;
                listItem.textContent = notification.message;
                listItem.classList.add("notification-item");

                // Jeśli powiadomienie jest nieodczytane, dodaj klasę wyróżniającą
                if (notification.status === "UNREAD") {
                    listItem.classList.add("unread");
                }

                // Obsługa kliknięcia - zmienia status, ale NIE USUWA z listy
                listItem.addEventListener("click", function () {
                    markNotificationAsRead(notification.id, listItem);
                });

                // Przycisk "Zobacz"
                let viewButton = document.createElement("button");
                viewButton.textContent = "Zobacz";
                viewButton.classList.add("view-button");
                viewButton.addEventListener("click", function (event) {
                    event.stopPropagation();
                    window.location.href = "/client-panel";
                });

                // Przycisk "❌" do usuwania powiadomienia
                let deleteButton = document.createElement("button");
                deleteButton.textContent = "❌";
                deleteButton.classList.add("delete-button");
                deleteButton.addEventListener("click", function (event) {
                    event.stopPropagation();
                    deleteNotification(notification.id, listItem);
                });

                listItem.appendChild(viewButton);
                listItem.appendChild(deleteButton);
                notificationsList.appendChild(listItem);
            });

            bellButton.classList.add("new-notifications");
        })
        .catch(error => console.error("Błąd pobierania powiadomień:", error));
}

function deleteNotification(notificationId, listItem) {
    fetch(`/notifications/delete/${notificationId}`, { method: "DELETE" })
        .then(() => {
            listItem.remove();
            if (notificationsList.children.length === 0) {
                notificationsList.innerHTML = "<li>Brak nowych powiadomień</li>";
                bellButton.classList.remove("new-notifications");
            }
        })
        .catch(error => console.error("Błąd usuwania powiadomienia:", error));
}



function markNotificationAsRead(notificationId, listItem) {
    fetch(`/notifications/mark-read/${notificationId}`, { method: "POST" })
        .then(() => {
            listItem.classList.remove("unread"); // Zmienia kolor, ale NIE usuwa powiadomienia
        })
        .catch(error => console.error("Błąd oznaczania jako przeczytane:", error));
}




function toggleNotifications() {
    const dropdown = document.getElementById("notifications-dropdown");

    if (!dropdown) {
        console.error("Nie znaleziono #notifications-dropdown w DOM.");
        return;
    }

    // Jeśli dropdown jest ukryty, pokaż go, jeśli widoczny - ukryj
    if (dropdown.style.display === "none" || dropdown.style.display === "") {
        dropdown.style.display = "block"; // Pokaż dropdown
        fetchNotifications(); // Pobierz powiadomienia
    } else {
        dropdown.style.display = "none"; // Ukryj dropdown
    }
}

    bellButton.addEventListener("click", toggleNotifications);
    setInterval(fetchNotifications, 5000); // Odświeżanie co 5s
});
