/**
 * Smart Campus - Chatbot Module
 */

let chatbotOpen = false;

function toggleChatbot() {
    const chatWindow = document.getElementById('chatbotWindow');
    chatbotOpen = !chatbotOpen;
    if (chatbotOpen) {
        chatWindow.classList.add('active');
        document.getElementById('chatInput').focus();
    } else {
        chatWindow.classList.remove('active');
    }
}

function sendChatMessage() {
    const input = document.getElementById('chatInput');
    const message = input.value.trim();
    if (!message) return;

    // Add user message
    addChatMessage(message, 'user');
    input.value = '';

    // Show typing indicator
    const typingId = addTypingIndicator();

    // Send to API
    fetch('/api/chatbot/message', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: message })
    })
    .then(response => response.json())
    .then(data => {
        removeTypingIndicator(typingId);
        addChatMessage(data.response, 'bot');
    })
    .catch(error => {
        removeTypingIndicator(typingId);
        addChatMessage('Sorry, something went wrong. Please try again.', 'bot');
    });
}

function addChatMessage(text, sender) {
    const messagesDiv = document.getElementById('chatMessages');
    const msgDiv = document.createElement('div');
    msgDiv.className = 'chat-message ' + sender;

    // Simple markdown-like formatting
    text = text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
    text = text.replace(/\n/g, '<br>');
    msgDiv.innerHTML = text;

    messagesDiv.appendChild(msgDiv);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

function addTypingIndicator() {
    const messagesDiv = document.getElementById('chatMessages');
    const typingDiv = document.createElement('div');
    typingDiv.className = 'chat-message bot typing-indicator';
    typingDiv.id = 'typing-' + Date.now();
    typingDiv.innerHTML = '<span class="dot"></span><span class="dot"></span><span class="dot"></span>';
    messagesDiv.appendChild(typingDiv);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
    return typingDiv.id;
}

function removeTypingIndicator(id) {
    const el = document.getElementById(id);
    if (el) el.remove();
}

// Handle Enter key
document.addEventListener('DOMContentLoaded', function() {
    const chatInput = document.getElementById('chatInput');
    if (chatInput) {
        chatInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') sendChatMessage();
        });
    }
});
