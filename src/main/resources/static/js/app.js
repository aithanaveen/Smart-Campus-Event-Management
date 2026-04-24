/**
 * Smart Campus - Main Application JavaScript
 */

function sendOTP() {
    const email = document.getElementById('otpEmail').value;
    if (!email) {
        showToast('Please enter your email', 'error');
        return;
    }

    const btn = document.getElementById('sendOtpBtn');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Sending...';

    fetch('/api/otp/send', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => {
                throw new Error(err.message || 'Failed to send OTP');
            });
        }
        return response.json();
    })
    .then(data => {
        showToast(data.message || 'OTP sent to your email!', 'success');
        document.getElementById('otpSection').style.display = 'block';
        btn.innerHTML = 'Resend OTP';
        btn.disabled = false;
    })
    .catch(error => {
        showToast(error.message || 'Failed to send OTP', 'error');
        btn.innerHTML = 'Send OTP';
        btn.disabled = false;
    });
}

function verifyOTP() {
    const email = document.getElementById('otpEmail').value;
    const otp = document.getElementById('otpInput').value.trim();

    if (!/^\d{6}$/.test(otp)) {
        showToast('Please enter a valid 6-digit OTP', 'error');
        return;
    }

    fetch('/api/otp/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email, otp: otp })
    })
    .then(response => response.json().then(data => ({
        ok: response.ok,
        data: data
    })))
    .then(data => {
        if (data.ok && data.data.verified) {
            showToast('OTP verified!', 'success');
            document.getElementById('otpVerified').value = 'true';
            document.getElementById('otpBadge').innerHTML = '<span class="badge bg-success">Verified</span>';
            const bookBtn = document.getElementById('bookSeatBtn');
            if (bookBtn && document.getElementById('seatLabelInput').value) {
                bookBtn.disabled = false;
            }
        } else {
            showToast(data.data.message || 'Invalid OTP. Please try again.', 'error');
        }
    })
    .catch(() => showToast('Verification failed', 'error'));
}

function registerForEvent(eventId) {
    const seatLabel = document.getElementById('seatLabelInput').value;
    if (!seatLabel) {
        showToast('Please select a seat first', 'error');
        return;
    }

    const studentId = document.getElementById('studentIdInput').value;
    const isVerified = document.getElementById('otpVerified').value === 'true';

    if (!isVerified) {
        showToast('Please verify your email with OTP first', 'error');
        return;
    }

    const btn = document.getElementById('bookSeatBtn');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Registering...';

    fetch('/api/seats/book', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            eventId: eventId,
            seatLabel: seatLabel,
            studentId: studentId
        })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.error || err.message || 'Registration failed') });
        }
        return response.json();
    })
    .then(data => {
        showToast('Registration successful! Code: ' + (data.registrationCode || 'N/A'), 'success');
        setTimeout(() => window.location.href = '/student/my-registrations', 2000);
    })
    .catch(error => {
        showToast(error.message || 'Registration failed', 'error');
        btn.disabled = false;
        btn.innerHTML = 'Confirm Booking';
    });
}

function showToast(message, type) {
    const toastContainer = document.getElementById('toastContainer') || createToastContainer();
    const toast = document.createElement('div');
    toast.className = `alert alert-${type === 'success' ? 'success' : 'danger'} alert-dismissible fade show`;
    toast.style.cssText = 'border-radius: 10px; border: none; box-shadow: 0 4px 15px rgba(0,0,0,0.1); margin-bottom: 10px; animation: fadeIn 0.3s ease;';
    toast.innerHTML = `${message}<button type="button" class="btn-close" data-bs-dismiss="alert"></button>`;
    toastContainer.appendChild(toast);
    setTimeout(() => toast.remove(), 5000);
}

function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toastContainer';
    container.style.cssText = 'position: fixed; top: 20px; right: 20px; z-index: 99999; max-width: 400px;';
    document.body.appendChild(container);
    return container;
}

function initMap(lat, lng, venueName) {
    if (typeof google === 'undefined') return;
    const location = { lat: lat, lng: lng };
    const map = new google.maps.Map(document.getElementById('eventMap'), {
        zoom: 15,
        center: location,
        styles: [
            { elementType: 'geometry', stylers: [{ color: '#242f3e' }] },
            { elementType: 'labels.text.stroke', stylers: [{ color: '#242f3e' }] },
            { elementType: 'labels.text.fill', stylers: [{ color: '#746855' }] }
        ]
    });
    new google.maps.Marker({ position: location, map: map, title: venueName });
}
