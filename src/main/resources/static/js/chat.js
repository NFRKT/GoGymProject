document.addEventListener("DOMContentLoaded", function () {
    console.log("Chat.js załadowany");
    // Ustawienie ID zalogowanego użytkownika
    window.currentUserId = document.getElementById("currentUserId")?.value || null;
    console.log("Zalogowany użytkownik ID:", window.currentUserId);
    loadChatRooms();
    connectToGlobalUpdates();
});

function toggleChat() {
    let chatContainer = document.getElementById("chat-container");
    chatContainer.style.display = (chatContainer.style.display === "block") ? "none" : "block";
    if (chatContainer.style.display === "block") {
        loadChatRooms();
    }
}

function loadChatRooms() {
    fetch("/chat")
        .then(response => response.json())
        .then(chatRooms => {
            let chatRoomsDiv = document.getElementById("chat-rooms");
            chatRoomsDiv.innerHTML = "";
            chatRooms.forEach(room => {
                let roomElement = document.createElement("div");
                roomElement.classList.add("chat-room-link");
                roomElement.dataset.chatRoomId = room.id; // Ustawienie ID pokoju
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
    disconnectChat();
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

    // 🔄 Poczekaj 100ms przed oznaczeniem wiadomości jako przeczytane (żeby WebSocket się połączył)
    setTimeout(() => {
        markMessagesAsRead(chatRoomId);
    }, 100);
}


function autoResizeTextarea() {
    this.style.height = "auto"; // Reset wysokości, by uniknąć nadmiernego powiększania
    this.style.height = Math.min(this.scrollHeight, 100) + "px"; // Maksymalna wysokość: 100px
}

function markMessagesAsRead(chatRoomId) {
    fetch(`/chat/${chatRoomId}/read`, { method: "POST" })
        .then(() => console.log("Wiadomości oznaczone jako przeczytane"))
        .catch(error => console.error("Błąd oznaczania wiadomości jako przeczytane:", error));
}

// Globalny klient WebSocket do globalnych aktualizacji liczby nieodczytanych wiadomości
let globalStompClient = null;
function connectToGlobalUpdates() {
    const socket = new SockJS('/ws');
    globalStompClient = Stomp.over(socket);
    globalStompClient.connect({}, function() {
        console.log("📡 Połączono z globalnym kanałem aktualizacji");
        globalStompClient.subscribe(`/topic/chat/updateUnread`, function (update) {
            console.log("📩 Otrzymano globalną aktualizację:", update.body);
            let chatRoomUpdate = JSON.parse(update.body);
            console.log("📊 Przetworzona aktualizacja:", chatRoomUpdate);
            updateUnreadCount(chatRoomUpdate);
        });
    });
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
        // Uwaga: globalna subskrypcja aktualizacji nieodczytanych wiadomości działa niezależnie
        loadChatHistory(chatRoomId);
    });
}

function disconnectChat() {
    if (stompClient && stompClient.connected) {
        stompClient.disconnect(() => {
            console.log("Chat został rozłączony.");
        });
    }
}

function updateUnreadCount(chatRoomUpdate) {
    console.log("🔄 Aktualizacja pokoju:", chatRoomUpdate);

    // ✅ Aktualizuj tylko, jeśli użytkownik to targetUserId
    if (window.currentUserId != chatRoomUpdate.targetUserId) {
        console.log("⏭ Pomijam aktualizację dla innego użytkownika:", chatRoomUpdate.targetUserId);
        return;
    }

    let chatRoomsDiv = document.getElementById("chat-rooms");
    let chatRoomElements = chatRoomsDiv.getElementsByClassName("chat-room-link");

    for (let roomElement of chatRoomElements) {
        if (Number(roomElement.dataset.chatRoomId) === Number(chatRoomUpdate.id)) {
            console.log("📌 Aktualizuję UI pokoju:", chatRoomUpdate.targetUserName, "Unread:", chatRoomUpdate.unreadCount);

            let unreadSpan = roomElement.querySelector(".unread-count");
            if (chatRoomUpdate.unreadCount > 0) {
                if (!unreadSpan) {
                    unreadSpan = document.createElement("span");
                    unreadSpan.classList.add("unread-count");
                    roomElement.appendChild(unreadSpan);
                }
                unreadSpan.innerText = `(${chatRoomUpdate.unreadCount})`;
            } else {
                if (unreadSpan) unreadSpan.remove();
            }
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
        // Dodaj separator z datą – wyśrodkowany dzięki CSS
        let dateHeader = document.createElement("div");
        dateHeader.classList.add("message-date");
        dateHeader.textContent = msgDate;
        messagesDiv.appendChild(dateHeader);
    }
    // Utwórz element wiadomości
    let messageElement = document.createElement("div");
    let isCurrentUser = Number(message.senderId) === Number(window.currentUserId);
    let senderDisplay = isCurrentUser ? "Ty:" : message.senderName + ":";
    messageElement.classList.add("message", isCurrentUser ? "user-message" : "trainer-message");
    messageElement.setAttribute("data-message-id", message.id);
    let time = "";
    if (message.sentAt) {
        let dateTime = new Date(message.sentAt);
        time = dateTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
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
