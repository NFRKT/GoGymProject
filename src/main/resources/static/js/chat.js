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
                roomElement.innerText = room.targetUserName; // wyświetla imię i nazwisko rozmówcy
                roomElement.onclick = function () {
                    openChatRoom(room.id, room.targetUserName);
                };
                chatRoomsDiv.appendChild(roomElement);
            });
        })
        .catch(error => {
            console.error("Błąd ładowania pokoi chatu:", error);
            document.getElementById("chat-rooms").innerHTML = "<p>Błąd ładowania rozmów</p>";
        });
}



function openChatRoom(chatRoomId, targetUserName) {
    // Zapamiętaj ID pokoju dla usuwania wiadomości
    window.currentChatRoomId = chatRoomId;

    let chatRoomsDiv = document.getElementById("chat-rooms");
    chatRoomsDiv.innerHTML = `
        <div id="chat-conversation-header">
            <button onclick="loadChatRooms()" class="back-button">← Powrót</button>
            <span class="chat-partner-name">${targetUserName}</span>
        </div>
        <div id="chat-messages"></div>
        <input type="text" id="messageInput" placeholder="Wpisz wiadomość...">
        <button onclick="sendMessage(${chatRoomId})">Wyślij</button>
    `;

    connectToChat(chatRoomId);
}



let stompClient = null;

function connectToChat(chatRoomId) {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        console.log("Połączono z chatem");

        // Subskrypcja na normalne wiadomości
        stompClient.subscribe(`/topic/chat/${chatRoomId}`, function (message) {
            addMessage(JSON.parse(message.body));
        });

        // Subskrypcja na zdarzenie usunięcia wiadomości
        stompClient.subscribe(`/topic/chat/${chatRoomId}/delete`, function (payload) {
            let deletedMessageId = payload.body;
            console.log("Usunięto wiadomość o ID:", deletedMessageId);
            // Usuń element(y) z DOM
            let elements = document.querySelectorAll(`[data-message-id='${deletedMessageId}']`);
            elements.forEach(el => el.remove());
        });

        loadChatHistory(chatRoomId);
    });
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

