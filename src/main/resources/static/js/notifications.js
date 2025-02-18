document.addEventListener("DOMContentLoaded", function () {
    const bellButton = document.getElementById("notifications-bell");
    const dropdown = document.getElementById("notifications-dropdown");
    const notificationsList = document.getElementById("notifications-list");

    if (!bellButton || !dropdown || !notificationsList) {
        console.error("Brak elementów powiadomień w DOM.");
        return;
    }

function fetchNotifications() {
    fetch("/notifications/unread")
        .then(response => response.json())
        .then(data => {
            notificationsList.innerHTML = "";

            if (data.length === 0) {
                notificationsList.innerHTML = "<li>Brak nowych powiadomień</li>";
                bellButton.classList.remove("new-notifications"); // Usuwamy czerwony kolor
                return;
            }

            data.forEach(notification => {
                let listItem = document.createElement("li");
                listItem.textContent = notification.message; // Teraz używamy poprawnie obiektu JSON
                listItem.classList.add("notification-item");
                listItem.onclick = function () {
                    markNotificationAsRead(notification.id, listItem);
                };
                notificationsList.appendChild(listItem);
            });

            bellButton.classList.add("new-notifications"); // Dodajemy czerwony kolor
        })
        .catch(error => console.error("Błąd pobierania powiadomień:", error));
}


function markNotificationAsRead(notificationId, listItem) {
    fetch(`/notifications/mark-read/${notificationId}`, {
        method: "POST"
    })
    .then(() => {
        listItem.remove(); // Usuwamy powiadomienie z listy
        if (notificationsList.children.length === 0) {
            notificationsList.innerHTML = "<li>Brak nowych powiadomień</li>";
            bellButton.classList.remove("new-notifications"); // Usuwamy czerwony kolor
        }
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
