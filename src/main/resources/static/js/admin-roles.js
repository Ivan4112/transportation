// JavaScript for User Role Management

document.addEventListener('DOMContentLoaded', function() {
    // Check if user is authenticated and has ADMIN role
    if (!isAuthenticated()) {
        window.location.href = '/login';
        return;
    }
    
    const userRole = getUserRole();
    if (userRole !== 'ADMIN') {
        alert('You do not have permission to access this page');
        window.location.href = '/';
        return;
    }
    
    // Load role assignments
    loadRoleAssignments();
    
    // Set up form submission handler
    document.getElementById('role-assignment-form').addEventListener('submit', function(e) {
        e.preventDefault();
        assignRole();
    });
    
    // Set up refresh button handler
    document.getElementById('refresh-button').addEventListener('click', function() {
        loadRoleAssignments();
    });
    
    // Set up search input handler
    document.getElementById('search-input').addEventListener('input', function() {
        filterRoleAssignments();
    });
});

function isAuthenticated() {
    return localStorage.getItem('token') !== null;
}

function getUserRole() {
    const token = localStorage.getItem('token');
    if (!token) return null;
    
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        const payload = JSON.parse(jsonPayload);
        return payload.role;
    } catch (e) {
        console.error('Error parsing token:', e);
        return null;
    }
}

// Global variable to store all role assignments
let allRoleAssignments = [];

function loadRoleAssignments() {
    const token = localStorage.getItem('token');
    
    // Show loading indicator
    document.getElementById('roles-loading').style.display = 'block';
    document.getElementById('roles-table-container').style.display = 'none';
    document.getElementById('no-roles-message').style.display = 'none';
    
    fetch('/api/admin/users/roles', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to load role assignments');
        }
        return response.json();
    })
    .then(roles => {
        console.log('Role assignments loaded:', roles);
        
        // Filter roles that have email assignments
        allRoleAssignments = roles.filter(role => role.email);
        
        // Display role assignments
        displayRoleAssignments(allRoleAssignments);
        
        // Hide loading indicator
        document.getElementById('roles-loading').style.display = 'none';
        
        if (allRoleAssignments.length > 0) {
            document.getElementById('roles-table-container').style.display = 'block';
        } else {
            document.getElementById('no-roles-message').style.display = 'block';
        }
    })
    .catch(error => {
        console.error('Error loading role assignments:', error);
        document.getElementById('roles-loading').textContent = 'Error loading role assignments. Please try again.';
        showAlert('error', 'Failed to load role assignments');
    });
}

function displayRoleAssignments(roles) {
    const tableBody = document.getElementById('roles-table-body');
    tableBody.innerHTML = '';
    
    if (roles.length === 0) {
        document.getElementById('roles-table-container').style.display = 'none';
        document.getElementById('no-roles-message').style.display = 'block';
        return;
    }
    
    document.getElementById('roles-table-container').style.display = 'block';
    document.getElementById('no-roles-message').style.display = 'none';
    
    // Sort by ID (newest first)
    roles.sort((a, b) => b.id - a.id);
    
    roles.forEach(role => {
        const row = document.createElement('tr');
        
        // Create badge class based on role
        let badgeClass = '';
        switch (role.roleName) {
            case 'ADMIN':
                badgeClass = 'badge-danger';
                break;
            case 'SUPPORT_AGENT':
                badgeClass = 'badge-warning';
                break;
            case 'DRIVER':
                badgeClass = 'badge-info';
                break;
            case 'CUSTOMER':
                badgeClass = 'badge-success';
                break;
            default:
                badgeClass = 'badge-secondary';
        }
        
        row.innerHTML = `
            <td>${role.id}</td>
            <td>${role.email}</td>
            <td><span class="badge ${badgeClass}">${role.roleName}</span></td>
            <td>
                <button class="btn btn-sm btn-danger remove-role" data-email="${role.email}">Remove</button>
            </td>
        `;
        
        tableBody.appendChild(row);
    });
    
    // Attach event handlers to remove buttons
    document.querySelectorAll('.remove-role').forEach(button => {
        button.addEventListener('click', function() {
            const email = this.getAttribute('data-email');
            if (confirm(`Are you sure you want to remove the role assignment for ${email}?`)) {
                removeRoleAssignment(email, this);
            }
        });
    });
}

function filterRoleAssignments() {
    const searchTerm = document.getElementById('search-input').value.toLowerCase();
    
    if (!allRoleAssignments) return;
    
    const filteredRoles = allRoleAssignments.filter(role => 
        role.email.toLowerCase().includes(searchTerm)
    );
    
    displayRoleAssignments(filteredRoles);
}

function assignRole() {
    const email = document.getElementById('email').value;
    const roleName = document.getElementById('roleName').value;
    const token = localStorage.getItem('token');
    
    // Disable form during submission
    const submitButton = document.querySelector('#role-assignment-form button[type="submit"]');
    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = 'Assigning...';
    
    fetch('/api/admin/users/roles', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
            email: email,
            roleName: roleName
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to assign role');
        }
        return response.json();
    })
    .then(data => {
        console.log('Role assigned successfully:', data);
        
        // Show success message
        showAlert('success', `Role ${roleName} assigned to ${email} successfully`);
        
        // Reset form
        document.getElementById('email').value = '';
        document.getElementById('roleName').value = '';
        
        // Reload role assignments
        loadRoleAssignments();
    })
    .catch(error => {
        console.error('Error assigning role:', error);
        showAlert('error', 'Failed to assign role. Please try again.');
    })
    .finally(() => {
        // Re-enable form
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
    });
}

function removeRoleAssignment(email, buttonElement) {
    const token = localStorage.getItem('token');
    
    // Disable button during removal
    buttonElement.disabled = true;
    buttonElement.textContent = 'Removing...';
    
    fetch(`/api/admin/users/roles?email=${encodeURIComponent(email)}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to remove role assignment');
        }
        
        // Show success message
        showAlert('success', `Role assignment for ${email} removed successfully`);
        
        // Reload role assignments
        loadRoleAssignments();
    })
    .catch(error => {
        console.error('Error removing role assignment:', error);
        showAlert('error', 'Failed to remove role assignment. Please try again.');
        
        // Re-enable button
        buttonElement.disabled = false;
        buttonElement.textContent = 'Remove';
    });
}

function showAlert(type, message) {
    const alertId = type === 'success' ? 'success-alert' : 'error-alert';
    const alertElement = document.getElementById(alertId);
    
    alertElement.textContent = message;
    alertElement.style.display = 'block';
    
    // Hide alert after 5 seconds
    setTimeout(() => {
        alertElement.style.display = 'none';
    }, 5000);
}
