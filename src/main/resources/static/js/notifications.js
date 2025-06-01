/**
 * Notifications related JavaScript functions
 */

// Load user notifications
function loadNotifications() {
    const notificationsContainer = document.getElementById('notificationsContainer');
    if (!notificationsContainer) return;
    
    // Show loading indicator
    notificationsContainer.innerHTML = '<div class="text-center"><p>Loading notifications...</p></div>';
    
    // Fetch notifications from API
    fetchWithAuth('/api/notifications')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load notifications');
            }
            return response.json();
        })
        .then(notifications => {
            if (notifications.length === 0) {
                notificationsContainer.innerHTML = `
                    <div class="alert alert-info">
                        You don't have any notifications.
                    </div>
                `;
                return;
            }
            
            // Create notifications list
            let html = '<div class="notification-list">';
            
            notifications.forEach(notification => {
                html += `
                    <div class="notification-item${notification.isRead ? '' : ' unread'}">
                        <div style="display: flex; justify-content: space-between;">
                            <div>
                                <p>${notification.message}</p>
                                <p class="time">${formatDate(notification.createdAt)}</p>
                            </div>
                            <div>
                                <a href="/customer/orders/${notification.orderId}" class="btn btn-info btn-sm">View Order</a>
                                ${!notification.isRead ? `
                                    <button class="btn btn-secondary btn-sm" onclick="markAsRead(${notification.id})">Mark as Read</button>
                                ` : ''}
                            </div>
                        </div>
                    </div>
                `;
            });
            
            html += '</div>';
            
            notificationsContainer.innerHTML = html;
        })
        .catch(error => {
            console.error('Error:', error);
            notificationsContainer.innerHTML = `
                <div class="alert alert-danger">
                    Failed to load notifications. Please try again later.
                </div>
            `;
        });
}

// Mark notification as read
function markAsRead(notificationId) {
    fetchWithAuth(`/api/notifications/${notificationId}/read`, {
        method: 'PATCH'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to mark notification as read');
        }
        // Refresh notifications
        loadNotifications();
        // Update unread count
        updateUnreadCount();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to mark notification as read. Please try again.');
    });
}

// Mark all notifications as read
function markAllAsRead() {
    fetchWithAuth('/api/notifications/read-all', {
        method: 'PATCH'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to mark all notifications as read');
        }
        // Refresh notifications
        loadNotifications();
        // Update unread count
        updateUnreadCount();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to mark all notifications as read. Please try again.');
    });
}

// Update unread notifications count
function updateUnreadCount() {
    const countBadge = document.getElementById('notificationCount');
    if (!countBadge) return;
    
    fetchWithAuth('/api/notifications/unread/count')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to get unread count');
            }
            return response.json();
        })
        .then(data => {
            const count = data.count;
            
            if (count > 0) {
                countBadge.textContent = count;
                countBadge.style.display = 'flex';
            } else {
                countBadge.style.display = 'none';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            countBadge.style.display = 'none';
        });
}

// Format date
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString();
}

// Initialize notifications page
document.addEventListener('DOMContentLoaded', function() {
    // Check if user is authenticated
    if (isAuthenticated()) {
        // Update unread count
        updateUnreadCount();
        
        // Load notifications if on notifications page
        if (window.location.pathname === '/notifications') {
            loadNotifications();
            
            // Add event listener to mark all as read button
            const markAllReadButton = document.getElementById('markAllReadButton');
            if (markAllReadButton) {
                markAllReadButton.addEventListener('click', markAllAsRead);
            }
        }
    }
    
    // Set up polling for notification count updates (every 30 seconds)
    setInterval(updateUnreadCount, 30000);
});
