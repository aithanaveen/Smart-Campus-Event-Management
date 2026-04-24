/**
 * Smart Campus - Seat Booking Module
 * Real-time seat selection with WebSocket updates
 */

let stompClient = null;
let selectedSeat = null;
let eventId = null;

function initSeatBooking(evtId) {
    eventId = evtId;
    loadSeats();
    connectWebSocket();
}

function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null; // Disable debug logging

    stompClient.connect({}, function(frame) {
        console.log('WebSocket connected');
        stompClient.subscribe('/topic/seats/' + eventId, function(message) {
            const seatUpdate = JSON.parse(message.body);
            updateSeatUI(seatUpdate);
        });
    }, function(error) {
        console.error('WebSocket error:', error);
        setTimeout(connectWebSocket, 5000); // Reconnect
    });
}

function loadSeats() {
    fetch('/api/seats/' + eventId)
        .then(response => response.json())
        .then(seats => renderSeatMap(seats))
        .catch(error => console.error('Error loading seats:', error));
}

function renderSeatMap(seats) {
    const container = document.getElementById('seatGrid');
    if (!container) return;

    container.innerHTML = '';

    // Group seats by row
    const rows = {};
    seats.forEach(seat => {
        if (!rows[seat.seatRow]) {
            rows[seat.seatRow] = [];
        }
        rows[seat.seatRow].push(seat);
    });

    // Render rows
    Object.keys(rows).sort((a, b) => a - b).forEach(rowNum => {
        const rowDiv = document.createElement('div');
        rowDiv.className = 'seat-row';

        // Row label
        const label = document.createElement('span');
        label.className = 'seat-row-label';
        label.textContent = String.fromCharCode(64 + parseInt(rowNum));
        rowDiv.appendChild(label);

        // Seats in row
        rows[rowNum].sort((a, b) => a.seatCol - b.seatCol).forEach(seat => {
            const seatDiv = document.createElement('div');
            seatDiv.className = 'seat ' + seat.status.toLowerCase();
            if (seat.seatType === 'VIP') seatDiv.classList.add('vip');
            seatDiv.setAttribute('data-seat-label', seat.seatLabel);
            seatDiv.setAttribute('data-seat-id', seat.id);
            seatDiv.textContent = seat.seatCol;
            seatDiv.title = seat.seatLabel + (seat.seatType === 'VIP' ? ' (VIP)' : '') +
                (seat.status === 'BOOKED' ? ' - Booked' : '');

            if (seat.status === 'AVAILABLE') {
                seatDiv.addEventListener('click', () => selectSeat(seatDiv, seat));
            }

            rowDiv.appendChild(seatDiv);
        });

        // Right label
        const labelRight = document.createElement('span');
        labelRight.className = 'seat-row-label';
        labelRight.textContent = String.fromCharCode(64 + parseInt(rowNum));
        rowDiv.appendChild(labelRight);

        container.appendChild(rowDiv);
    });
}

function selectSeat(seatDiv, seat) {
    // Deselect previous
    if (selectedSeat) {
        const prev = document.querySelector('.seat.selected');
        if (prev) {
            prev.classList.remove('selected');
            prev.classList.add('available');
        }
    }

    // Select new
    seatDiv.classList.remove('available');
    seatDiv.classList.add('selected');
    selectedSeat = seat;

    // Update UI
    const selectedInfo = document.getElementById('selectedSeatInfo');
    if (selectedInfo) {
        selectedInfo.innerHTML = `
            <div class="alert alert-info-custom" style="background: rgba(66,153,225,0.1); border-left: 4px solid #4299e1; padding: 1rem; border-radius: 8px;">
                <strong>Selected Seat:</strong> ${seat.seatLabel} 
                ${seat.seatType === 'VIP' ? '<span class="badge bg-warning text-dark ms-2">VIP</span>' : ''}
            </div>`;
    }

    const seatInput = document.getElementById('seatLabelInput');
    if (seatInput) seatInput.value = seat.seatLabel;

    const bookBtn = document.getElementById('bookSeatBtn');
    const otpFlag = document.getElementById('otpVerified');
    const isOtpVerified = otpFlag && otpFlag.value === 'true';
    if (bookBtn && isOtpVerified) bookBtn.disabled = false;
}

function updateSeatUI(seatUpdate) {
    const seatDiv = document.querySelector(`[data-seat-label="${seatUpdate.seatLabel}"]`);
    if (seatDiv) {
        seatDiv.className = 'seat ' + seatUpdate.status.toLowerCase();
        if (seatUpdate.seatType === 'VIP') seatDiv.classList.add('vip');

        if (seatUpdate.status === 'BOOKED') {
            seatDiv.style.cursor = 'not-allowed';
            seatDiv.onclick = null;
            // If this was our selected seat, deselect
            if (selectedSeat && selectedSeat.seatLabel === seatUpdate.seatLabel) {
                selectedSeat = null;
                const selectedInfo = document.getElementById('selectedSeatInfo');
                if (selectedInfo) selectedInfo.innerHTML = '';
                const bookBtn = document.getElementById('bookSeatBtn');
                if (bookBtn) bookBtn.disabled = true;
            }
        }
    }

    // Update available count
    updateAvailableCount();
}

function updateAvailableCount() {
    const availableSeats = document.querySelectorAll('.seat.available').length;
    const countEl = document.getElementById('availableCount');
    if (countEl) countEl.textContent = availableSeats;
}

// Cleanup on page leave
window.addEventListener('beforeunload', function() {
    if (stompClient) stompClient.disconnect();
});
