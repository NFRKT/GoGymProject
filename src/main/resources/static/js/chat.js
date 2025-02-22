document.addEventListener("DOMContentLoaded", function () {
    console.log("Chat.js załadowany");
    loadChatRooms();
});

function toggleChat() {
    let chatContainer = document.getElementById("chat-container");
    chatContainer.style.display = (chatContainer.style.display === "block") ? "none" : "block";

    if (chatContainer.style.display === "block") {
        loadChatRooms();
    }
}

document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ `chat.js` załadowany!");
    loadChatRooms();

    // Pobierz ID aktualnie zalogowanego użytkownika
    window.currentUserId = document.getElementById("currentUserId")?.value || null;
    console.log("Zalogowany użytkownik ID:", window.currentUserId);
});



function loadChatRooms() {
    fetch("/chat")
        .then(response => response.json())
        .then(chatRooms => {
            let chatRoomsDiv = document.getElementById("chat-rooms");
            chatRoomsDiv.innerHTML = "";

            chatRooms.forEach(room => {
                let roomElement = document.createElement("div");
                roomElement.classList.add("chat-room-link");
                roomElement.dataset.chatRoomId = room.id; // ✅ Poprawione ustawienie ID
                roomElement.innerHTML = `${room.targetUserName}
                    ${room.unreadCount > 0 ? `<span class="unread-count">(${room.unreadCount})</span>` : ""}`;

                roomElement.onclick = function () {
                    openChatRoom(room.id, room.targetUserName);
                };
                chatRoomsDiv.appendChild(roomElement);
            });

            console.log("📋 Lista czatów załadowana.");
        })
        .catch(error => {
            console.error("Błąd ładowania pokoi chatu:", error);
            document.getElementById("chat-rooms").innerHTML = "<p>Błąd ładowania rozmów</p>";
        });
}




function openChatRoom(chatRoomId, targetUserName) {
    window.currentChatRoomId = chatRoomId;

    let chatRoomsDiv = document.getElementById("chat-rooms");
    chatRoomsDiv.innerHTML = `
        <div id="chat-conversation-header">
            <button onclick="loadChatRooms()" class="back-button">← Powrót</button>
            <span class="chat-partner-name">${targetUserName}</span>
        </div>
        <div id="chat-content">
            <div id="chat-messages"></div>
            <div id="chat-input-container">
                <textarea id="messageInput" placeholder="Wpisz wiadomość..." rows="1"></textarea>
                <button onclick="sendMessage(${chatRoomId})">Wyślij</button>
            </div>
        </div>
    `;

    connectToChat(chatRoomId);
    markMessagesAsRead(chatRoomId);
        let messageInput = document.getElementById("messageInput");
        messageInput.addEventListener("input", autoResizeTextarea);
}
function autoResizeTextarea() {
    this.style.height = "auto"; // Resetujemy wysokość, by uniknąć nadmiernego powiększania
    this.style.height = Math.min(this.scrollHeight, 100) + "px"; // Maksymalna wysokość to 150px
}



function markMessagesAsRead(chatRoomId) {
    fetch(`/chat/${chatRoomId}/read`, { method: "POST" })
        .then(() => console.log("Wiadomości oznaczone jako przeczytane"))
        .catch(error => console.error("Błąd oznaczania wiadomości jako przeczytane:", error));
}




let stompClient = null;

function connectToChat(chatRoomId) {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        console.log("Połączono z chatem");

        // Subskrypcja wiadomości w aktualnym pokoju
        stompClient.subscribe(`/topic/chat/${chatRoomId}`, function (message) {
            addMessage(JSON.parse(message.body));
        });

        // Subskrypcja usunięcia wiadomości
        stompClient.subscribe(`/topic/chat/${chatRoomId}/delete`, function (payload) {
            let deletedMessageId = payload.body;
            console.log("Usunięto wiadomość o ID:", deletedMessageId);
            let elements = document.querySelectorAll(`[data-message-id='${deletedMessageId}']`);
            elements.forEach(el => el.remove());
        });

        // 🔥 Subskrypcja na aktualizację liczby nieodczytanych wiadomości
        stompClient.subscribe(`/topic/chat/updateUnread`, function (update) {
            console.log("🔔 Otrzymano aktualizację nieodczytanych wiadomości:", update.body);
            let chatRoomUpdate = JSON.parse(update.body);
            updateUnreadCount(chatRoomUpdate);
        });


        loadChatHistory(chatRoomId);
    });
}
function updateUnreadCount(chatRoomUpdate) {
    console.log("🔄 Aktualizacja pokoju:", chatRoomUpdate);

    let chatRoomsDiv = document.getElementById("chat-rooms");

    // Jeśli aktualnie nie ma chatu, wymuś ponowne załadowanie
    if (!chatRoomsDiv.innerHTML || chatRoomsDiv.innerHTML.trim() === "") {
        loadChatRooms();
        return;
    }

    let chatRoomElements = chatRoomsDiv.getElementsByClassName("chat-room-link");

    for (let roomElement of chatRoomElements) {
        if (Number(roomElement.dataset.chatRoomId) === Number(chatRoomUpdate.id)) {
            console.log("📌 Aktualizuję pokój:", chatRoomUpdate.targetUserName);

            roomElement.innerHTML = `${chatRoomUpdate.targetUserName}
                ${chatRoomUpdate.unreadCount > 0 ? `<span class="unread-count">(${chatRoomUpdate.unreadCount})</span>` : ""}`;
        }
    }
}





function loadChatHistory(chatRoomId) {
    fetch(`/chat/${chatRoomId}/messages`)
        .then(response => response.json())
        .then(messages => {
            let messagesDiv = document.getElementById("chat-messages");
            messagesDiv.innerHTML = "";
            messages.forEach(msg => addMessage(msg));
        });
}


function sendMessage(chatRoomId) {
    let messageInput = document.getElementById("messageInput").value;
    if (!messageInput.trim()) return;

    let message = {
        chatRoomId: chatRoomId,
        message: messageInput
    };

    stompClient.send(`/app/chat/${chatRoomId}`, {}, JSON.stringify(message));
    document.getElementById("messageInput").value = "";
}

function getCurrentUserId() {
    let userId = document.getElementById("currentUserId")?.value || null;
    return userId;
}
function addMessage(message) {
    let messagesDiv = document.getElementById("chat-messages");

    // Formatowanie daty wiadomości (bez godziny)
    let msgDateObj = new Date(message.sentAt);
    let msgDate = msgDateObj.toLocaleDateString();

    // Sprawdź, czy ostatni dodany element to separator z tą datą
    let lastGroupDate = null;
    let children = messagesDiv.children;
    for (let i = children.length - 1; i >= 0; i--) {
        if (children[i].classList.contains("message-date")) {
            lastGroupDate = children[i].textContent;
            break;
        }
    }
    if (lastGroupDate !== msgDate) {
        // Dodaj separator z datą – będzie on wyśrodkowany dzięki CSS
        let dateHeader = document.createElement("div");
        dateHeader.classList.add("message-date");
        dateHeader.textContent = msgDate;
        messagesDiv.appendChild(dateHeader);
    }

    // Utwórz element wiadomości
    let messageElement = document.createElement("div");
    let isCurrentUser = Number(message.senderId) === Number(window.currentUserId);
    // Jeśli to Twoja wiadomość, zamiast imienia wyświetlamy "Ty:"
    let senderDisplay = isCurrentUser ? "Ty:" : message.senderName + ":";
    messageElement.classList.add("message", isCurrentUser ? "user-message" : "trainer-message");
    messageElement.setAttribute("data-message-id", message.id);
    // Formatowanie godziny wiadomości
    let time = "";
    if (message.sentAt) {
        let dateTime = new Date(message.sentAt);
        time = dateTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    messageElement.innerHTML = `<strong>${senderDisplay}</strong> ${message.message} <span class="timestamp">${time}</span>`;

    // Dodaj przycisk usuwania, jeśli to Twoja wiadomość
    let deleteButtonHTML = "";
    if (isCurrentUser) {
        deleteButtonHTML = `<button class="delete-message-button" onclick="deleteMessage(${message.id})">Usuń</button>`;
    }
    messageElement.innerHTML = `<strong>${senderDisplay}</strong> ${message.message} <span class="timestamp">${time}</span> ${deleteButtonHTML}`;

    messagesDiv.appendChild(messageElement);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}
function deleteMessage(messageId) {
    if (!confirm("Czy na pewno chcesz usunąć tę wiadomość?")) return;

    fetch(`/chat/${window.currentChatRoomId}/messages/${messageId}`, {
        method: "DELETE"
    })
    .then(response => {
        if (response.ok) {
            // Usuń element wiadomości z DOM – wiadomość znika natychmiast
            let messageElement = document.querySelector(`[data-message-id='${messageId}']`);
            if (messageElement) {
                messageElement.remove();
            }
        } else {
            alert("Błąd przy usuwaniu wiadomości");
        }
    })
    .catch(error => {
        console.error("Błąd przy usuwaniu wiadomości:", error);
    });
}



window.toggleChat = toggleChat;

