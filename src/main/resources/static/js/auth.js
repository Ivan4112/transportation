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

// Parse JWT token to get user information
function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error('Error parsing JWT token', e);
        return null;
    }
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
    
    // Show loading indicator
    const submitButton = document.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = 'Logging in...';
    
    // Clear previous error messages
    const errorElement = document.getElementById('loginError');
    if (errorElement) {
        errorElement.style.display = 'none';
    }
    
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
        
        // Store user info
        localStorage.setItem('user_role', data.role);
        localStorage.setItem('user_id', data.userId);
        localStorage.setItem('user_name', data.firstName + ' ' + data.lastName);
        localStorage.setItem('user_email', data.email);
        
        // Redirect based on role
        if (data.role === 'CUSTOMER') {
            window.location.href = '/customer/orders/create';
        } else if (data.role === 'DRIVER') {
            window.location.href = '/driver/dashboard';
        } else if (data.role === 'ADMIN') {
            window.location.href = '/admin/dashboard';
        } else {
            window.location.href = '/';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        // Show error message
        if (errorElement) {
            errorElement.textContent = 'Invalid email or password';
            errorElement.style.display = 'block';
        }
        
        // Reset button
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
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
    
    // Show loading indicator
    const submitButton = document.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = 'Registering...';
    
    // Clear previous error messages
    const errorElement = document.getElementById('registerError');
    if (errorElement) {
        errorElement.style.display = 'none';
    }
    
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
            return response.json().then(data => {
                throw new Error(data.message || 'Registration failed');
            });
        }
        return response.json();
    })
    .then(data => {
        // Store token
        storeToken(data.accessToken);
        
        // Store user info
        localStorage.setItem('user_role', data.role);
        localStorage.setItem('user_id', data.userId);
        localStorage.setItem('user_name', data.firstName + ' ' + data.lastName);
        localStorage.setItem('user_email', data.email);
        
        // Redirect to success page or login
        window.location.href = '/login?success=true';
    })
    .catch(error => {
        console.error('Error:', error);
        // Show error message
        if (errorElement) {
            errorElement.textContent = error.message || 'Registration failed. Please check your information and try again.';
            errorElement.style.display = 'block';
        }
        
        // Reset button
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
    });
}

// Handle logout
function handleLogout(event) {
    if (event) {
        event.preventDefault();
    }
    
    // Remove token and user info
    removeToken();
    localStorage.removeItem('user_role');
    localStorage.removeItem('user_id');
    localStorage.removeItem('user_name');
    localStorage.removeItem('user_email');
    
    // Redirect to login page
    window.location.href = '/login?logout=true';
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

// Check authentication status and update UI
function updateAuthUI() {
    const token = getToken();
    const authElements = document.querySelectorAll('[data-auth-required]');
    const nonAuthElements = document.querySelectorAll('[data-auth-hidden]');
    
    if (token) {
        // User is authenticated
        authElements.forEach(el => el.style.display = 'block');
        nonAuthElements.forEach(el => el.style.display = 'none');
        
        // Update user name if element exists
        const userNameElement = document.getElementById('userName');
        if (userNameElement) {
            userNameElement.textContent = localStorage.getItem('user_name') || localStorage.getItem('user_email');
        }
    } else {
        // User is not authenticated
        authElements.forEach(el => el.style.display = 'none');
        nonAuthElements.forEach(el => el.style.display = 'block');
    }
}

// Initialize authentication listeners
document.addEventListener('DOMContentLoaded', function() {
    // Update UI based on authentication status
    updateAuthUI();
    
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
