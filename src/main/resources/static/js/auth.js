/**
 * Authentication related JavaScript functions
 */

// Store JWT token in localStorage
function storeToken(token) {
    localStorage.setItem('jwt_token', token);
}

// Get JWT token from localStorage
function getToken() {
    return localStorage.getItem('jwt_token');
}

// Remove JWT token from localStorage
function removeToken() {
    localStorage.removeItem('jwt_token');
}

// Check if user is authenticated
function isAuthenticated() {
    return getToken() !== null;
}

// Handle login form submission
function handleLogin(event) {
    event.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    // Create request body
    const requestBody = {
        email: email,
        password: password
    };
    
    // Send API request
    fetch('/api/auth/sign-in', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Login failed');
        }
        return response.json();
    })
    .then(data => {
        // Store token
        storeToken(data.accessToken);
        
        // Redirect based on role
        if (data.role === 'CUSTOMER') {
            window.location.href = '/customer/orders/create';
        } else {
            window.location.href = '/';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        // Show error message
        const errorElement = document.getElementById('loginError');
        if (errorElement) {
            errorElement.style.display = 'block';
        }
    });
}

// Handle registration form submission
function handleRegister(event) {
    event.preventDefault();
    
    const firstName = document.getElementById('firstName').value;
    const lastName = document.getElementById('lastName').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    // Create request body
    const requestBody = {
        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password
    };
    
    // Send API request
    fetch('/api/auth/sign-up', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Registration failed');
        }
        return response.json();
    })
    .then(data => {
        // Store token
        storeToken(data.accessToken);
        
        // Redirect to create order page for customers
        if (data.role === 'CUSTOMER') {
            window.location.href = '/customer/orders/create';
        } else {
            window.location.href = '/';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        // Show error message
        const errorElement = document.getElementById('registerError');
        if (errorElement) {
            errorElement.style.display = 'block';
            errorElement.textContent = 'Registration failed. Please check your information and try again.';
        }
    });
}

// Handle logout
function handleLogout() {
    removeToken();
    window.location.href = '/login';
}

// Add authentication headers to fetch requests
function fetchWithAuth(url, options = {}) {
    const token = getToken();
    
    if (!token) {
        return Promise.reject(new Error('No authentication token found'));
    }
    
    const headers = {
        ...options.headers,
        'Authorization': `Bearer ${token}`
    };
    
    return fetch(url, {
        ...options,
        headers
    });
}

// Initialize authentication listeners
document.addEventListener('DOMContentLoaded', function() {
    // Login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    // Register form
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
    
    // Logout button
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    }
});
