document.addEventListener("DOMContentLoaded", function () {
    const bellButton = document.getElementById("notifications-bell");
    const dropdown = document.getElementById("notifications-dropdown");
    const notificationsList = document.getElementById("notifications-list");

    if (!bellButton || !dropdown || !notificationsList) {
        console.error("Brak elementów powiadomień w DOM.");
        return;
    }

function fetchNotifications() {
    fetch("/notifications/all")
        .then(response => response.json())
        .then(data => {
            notificationsList.innerHTML = "";

            if (data.length === 0) {
                notificationsList.innerHTML = "<li>Brak powiadomień</li>";
                bellButton.classList.remove("new-notifications");
                return;
            }

            let groupedNotifications = groupNotificationsByDate(data);

            Object.keys(groupedNotifications).forEach(date => {
                let dateHeader = document.createElement("li");
                dateHeader.textContent = date;
                dateHeader.classList.add("notification-date");
                notificationsList.appendChild(dateHeader);

                groupedNotifications[date].forEach(notification => {
                    let listItem = document.createElement("li");
                    listItem.dataset.id = notification.id;
                    listItem.classList.add("notification-item");

                    if (notification.status === "UNREAD") {
                        listItem.classList.add("unread");
                    }

                    let timeAgo = formatTimeAgo(notification.createdAt);
                    listItem.textContent = `${notification.message} (${timeAgo})`;

                    listItem.addEventListener("click", function () {
                        markNotificationAsRead(notification.id, listItem);
                    });

                    let viewButton = document.createElement("button");
                    viewButton.textContent = "Zobacz";
                    viewButton.classList.add("view-button");
                    viewButton.addEventListener("click", function (event) {
                        event.stopPropagation();

                        if (notification.message.includes("Twój trener")) {
                            window.location.href = `/user-plans?idUser=${notification.userId}`;
                        } else if(notification.message.includes("Twoja prośba o współpracę z trenerem") ||
                            notification.message.includes("zakończył z Tobą współpracę")){
                            window.location.href = "/client-panel";
                        }else if(notification.message.includes("Twój klient")){
                            window.location.href = `/trainer-plans?idUser=${notification.userId}`;
                        }else {
                            window.location.href = "/trainer-panel";
                        }
                    });

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

    function formatTimeAgo(timestamp) {
        let now = new Date();
        let notificationDate = new Date(parseInt(timestamp));
        let diff = now - notificationDate;

        let minutes = Math.floor(diff / 60000);
        let hours = Math.floor(diff / 3600000);
        let days = Math.floor(diff / 86400000);

        if (minutes < 60) {
            return minutes === 0 ? "Przed chwilą" : `${minutes} min. temu`;
        } else if (hours < 24) {
            return `${hours} godz. temu`;
        } else if (days === 1) {
            return "Wczoraj";
        } else {
            return `${days} dni temu`;
        }
    }

    function groupNotificationsByDate(notifications) {
        let grouped = {};

        notifications.forEach(notification => {
            let date = new Date(parseInt(notification.createdAt));
            let formattedDate = date.toLocaleDateString("pl-PL");

            if (!grouped[formattedDate]) {
                grouped[formattedDate] = [];
            }
            grouped[formattedDate].push(notification);
        });

        return grouped;
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
