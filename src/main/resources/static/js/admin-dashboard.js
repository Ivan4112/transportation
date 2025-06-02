// JavaScript for Admin Dashboard

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
    
    // Load dashboard statistics
    loadDashboardStats();
    
    // Load recent role assignments
    loadRecentRoleAssignments();
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

function loadDashboardStats() {
    // In a real application, you would fetch this data from an API
    // For now, we'll use placeholder values
    document.getElementById('total-users').textContent = '42';
    document.getElementById('customer-count').textContent = '30';
    document.getElementById('driver-count').textContent = '8';
    document.getElementById('support-count').textContent = '4';
}

function loadRecentRoleAssignments() {
    const token = localStorage.getItem('token');
    
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
        const roleAssignments = roles.filter(role => role.email);
        
        // Sort by ID (newest first) and take only the first 5
        const recentRoles = roleAssignments
            .sort((a, b) => b.id - a.id)
            .slice(0, 5);
        
        const tableBody = document.getElementById('recent-roles');
        tableBody.innerHTML = '';
        
        if (recentRoles.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="4" class="text-center">No role assignments found</td></tr>';
        } else {
            recentRoles.forEach(role => {
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
        
        document.getElementById('loading').style.display = 'none';
        document.getElementById('recent-roles-container').style.display = 'block';
    })
    .catch(error => {
        console.error('Error loading role assignments:', error);
        document.getElementById('loading').textContent = 'Error loading role assignments. Please try again.';
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
        
        // Reload role assignments
        loadRecentRoleAssignments();
    })
    .catch(error => {
        console.error('Error removing role assignment:', error);
        alert('Error removing role assignment. Please try again.');
        
        // Re-enable button
        buttonElement.disabled = false;
        buttonElement.textContent = 'Remove';
    });
}
