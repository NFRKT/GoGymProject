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
                roomElement.innerText = room.trainerName;
                roomElement.onclick = function () {
                    openChatRoom(room.id);
                };
                chatRoomsDiv.appendChild(roomElement);
            });
        })
        .catch(error => {
            console.error("Błąd ładowania pokoi chatu:", error);
            document.getElementById("chat-rooms").innerHTML = "<p>Błąd ładowania rozmów</p>";
        });
}

function openChatRoom(chatRoomId) {
    let chatRoomsDiv = document.getElementById("chat-rooms");
    chatRoomsDiv.innerHTML = `
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

        stompClient.subscribe(`/topic/chat/${chatRoomId}`, function (message) {
            showMessage(JSON.parse(message.body));
        });

        loadChatHistory(chatRoomId);
    });
}

function loadChatHistory(chatRoomId) {
    fetch(`/chat/${chatRoomId}/messages`)
        .then(response => response.json())
        .then(messages => {
            messages.forEach(msg => showMessage(msg));
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

function showMessage(message) {
    let messagesDiv = document.getElementById("chat-messages");
    let messageElement = document.createElement("p");
    messageElement.innerHTML = `<strong>${message.senderName}:</strong> ${message.message}`;
    messagesDiv.appendChild(messageElement);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

window.toggleChat = toggleChat;

