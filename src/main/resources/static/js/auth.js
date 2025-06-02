/**
 * Authentication related JavaScript functions
 */

// Store JWT token in localStorage and cookie
function storeToken(token) {
    localStorage.setItem('jwt_token', token);
    
    // Set cookie with SameSite=Lax to allow it to be sent with navigation requests
    document.cookie = `jwt_token=${token}; path=/; max-age=86400; SameSite=Lax`;
    
    console.log("Token stored in localStorage and cookie");
}

// Get JWT token from localStorage
function getToken() {
    return localStorage.getItem('jwt_token');
}

// Remove JWT token from localStorage and cookie
function removeToken() {
    localStorage.removeItem('jwt_token');
    document.cookie = 'jwt_token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT';
}

// Check if user is authenticated
function isAuthenticated() {
    return getToken() !== null;
}

// Handle login form submission
function handleLogin(event) {
    event.preventDefault();
    console.log("Login form submitted");
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    console.log("Attempting login for:", email);
    
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
    
    console.log("Sending API request to /api/auth/sign-in");
    
    // Send API request
    fetch('/api/auth/sign-in', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => {
        console.log("Received response:", response.status);
        if (!response.ok) {
            return response.text().then(text => {
                console.error("Error response body:", text);
                throw new Error('Login failed: ' + response.status);
            });
        }
        return response.json();
    })
    .then(data => {
        console.log("Login successful, received data:", data);
        
        // Store token in localStorage and cookie
        storeToken(data.accessToken);
        
        // Store user info
        localStorage.setItem('user_role', data.role);
        localStorage.setItem('user_id', data.userId);
        localStorage.setItem('user_name', data.firstName + ' ' + data.lastName);
        localStorage.setItem('user_email', data.email);
        console.log("User info stored in localStorage");
        
        // Show success message
        const successMessage = document.getElementById('loginSuccessMessage');
        if (successMessage) {
            successMessage.style.display = 'block';
        }
        
        console.log("Redirecting based on role:", data.role);
        
        // Add token to URL for the first navigation after login
        redirectBasedOnRole(data.role, data.accessToken);
    })
    .catch(error => {
        console.error('Error during login:', error);
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

// Redirect user based on role
function redirectBasedOnRole(role, token) {
    // Determine URL for redirect
    let redirectUrl;
    if (role === 'CUSTOMER') {
        redirectUrl = '/customer/orders/create';
    } else if (role === 'DRIVER') {
        redirectUrl = '/driver/orders';
    } else if (role === 'ADMIN') {
        redirectUrl = '/admin/dashboard';
    } else if (role === 'SUPPORT_AGENT') {
        redirectUrl = '/support/dashboard';
    } else {
        redirectUrl = '/';
    }
    
    // Don't include token in URL, it's already stored in localStorage and cookie
    window.location.href = redirectUrl;
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
        
        // Redirect to login page with success message
        window.location.replace('/login?success=true');
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
    window.location.replace('/login?logout=true');
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

// Update UI based on authentication status
function updateAuthUI() {
    const token = getToken();
    const authElements = document.querySelectorAll('[data-auth-required]');
    const nonAuthElements = document.querySelectorAll('[data-auth-hidden]');
    const roleElements = document.querySelectorAll('[data-role]');
    
    if (token) {
        // User is authenticated
        authElements.forEach(el => el.style.display = 'block');
        nonAuthElements.forEach(el => el.style.display = 'none');
        
        // Get user role
        const userRole = localStorage.getItem('user_role');
        
        // Show/hide elements based on role
        roleElements.forEach(el => {
            if (el.getAttribute('data-role') === userRole) {
                el.style.display = 'block';
            } else {
                el.style.display = 'none';
            }
        });
        
        // Update user name and role if elements exist
        const userNameElement = document.getElementById('userName');
        const userRoleElement = document.getElementById('userRole');
        
        if (userNameElement) {
            const name = localStorage.getItem('user_name') || localStorage.getItem('user_email');
            userNameElement.textContent = name;
        }
        
        if (userRoleElement) {
            userRoleElement.textContent = `(${localStorage.getItem('user_role')})`;
        }
    } else {
        // User is not authenticated
        authElements.forEach(el => el.style.display = 'none');
        nonAuthElements.forEach(el => el.style.display = 'block');
        roleElements.forEach(el => el.style.display = 'none');
    }
}

// Check authentication status on page load
document.addEventListener('DOMContentLoaded', function() {
    // Debug authentication status
    console.log("Checking authentication status");
    console.log("JWT in localStorage:", localStorage.getItem('jwt_token'));
    console.log("Cookies:", document.cookie);
    
    // Check if token is in URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const tokenParam = urlParams.get('token');
    if (tokenParam) {
        console.log("Found token in URL parameter, storing it");
        storeToken(tokenParam);
        
        // Remove token from URL to avoid exposing it
        const url = new URL(window.location);
        url.searchParams.delete('token');
        window.history.replaceState({}, document.title, url);
    }
    
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
