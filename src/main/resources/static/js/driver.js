/**
 * Driver related JavaScript functions
 */

// Load driver orders for the orders page
function loadDriverOrders() {
    const ordersContainer = document.getElementById('ordersContainer');
    if (!ordersContainer) {
        // If we're not on the orders page, check if we're on the vehicle page
        const assignmentsLoadingElement = document.getElementById('assignments-loading');
        const noAssignmentsElement = document.getElementById('no-assignments');
        const assignmentsListElement = document.getElementById('assignments-list');
        
        if (assignmentsLoadingElement && noAssignmentsElement && assignmentsListElement) {
            // We're on the vehicle page, load assignments
            loadDriverAssignments();
        }
        return;
    }
    
    // Show loading indicator
    ordersContainer.innerHTML = '<div class="text-center"><p>Loading orders...</p></div>';
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        ordersContainer.innerHTML = `
            <div class="alert alert-danger">
                Authentication error. Please log in again.
            </div>
        `;
        return;
    }
    
    console.log('Fetching driver orders with token:', token);
    
    // Direct fetch with token
    fetch('/api/driver/orders', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('API response status:', response.status);
            if (!response.ok) {
                throw new Error(`Failed to load orders: ${response.status}`);
            }
            return response.json();
        })
        .then(orders => {
            console.log('Received orders data:', orders);
            
            if (!orders || orders.length === 0) {
                ordersContainer.innerHTML = `
                    <div class="alert alert-info">
                        You don't have any assigned orders yet.
                    </div>
                `;
                return;
            }
            
            // Create orders table
            let html = `
                <table class="table">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Cargo</th>
                            <th>Route</th>
                            <th>Status</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
            `;
            
            orders.forEach(order => {
                console.log('Processing order:', order.id);
                html += `
                    <tr>
                        <td>${order.id}</td>
                        <td>
                            <span>${order.cargoType || 'N/A'}</span><br>
                            <small>${order.cargoWeight ? order.cargoWeight + ' kg' : 'N/A'}</small>
                        </td>
                        <td>
                            <span>${order.startLocation || 'N/A'} → ${order.endLocation || 'N/A'}</span><br>
                            <small>${order.distance ? order.distance + ' km' : 'N/A'}</small>
                        </td>
                        <td>
                            <span class="status-${order.status ? order.status.statusName.toLowerCase() : 'unknown'}">${order.status ? order.status.statusName : 'Unknown'}</span>
                        </td>
                        <td>${formatDate(order.createdAt)}</td>
                        <td>
                            <a href="/driver/orders/${order.id}" class="btn btn-info btn-sm">Details</a>
                        </td>
                    </tr>
                `;
            });
            
            html += `
                    </tbody>
                </table>
            `;
            
            ordersContainer.innerHTML = html;
        })
        .catch(error => {
            console.error('Error:', error);
            console.error('Error details:', error.message);
            ordersContainer.innerHTML = `
                <div class="alert alert-danger">
                    Failed to load orders. Please try again later.
                    <br>
                    <small>Error details: ${error.message}</small>
                    <br>
                    <button class="btn btn-sm btn-primary mt-2" onclick="loadDriverOrders()">Try Again</button>
                </div>
            `;
        });
}

// Load driver assignments for the vehicle page
function loadDriverAssignments() {
    const assignmentsLoadingElement = document.getElementById('assignments-loading');
    const noAssignmentsElement = document.getElementById('no-assignments');
    const assignmentsListElement = document.getElementById('assignments-list');
    
    if (!assignmentsLoadingElement || !noAssignmentsElement || !assignmentsListElement) return;
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        assignmentsLoadingElement.style.display = 'none';
        assignmentsListElement.innerHTML = `
            <div class="alert alert-danger">
                Authentication error. Please log in again.
            </div>
        `;
        assignmentsListElement.style.display = 'block';
        return;
    }
    
    console.log('Loading driver assignments');
    
    // Fetch orders from API
    fetch('/api/driver/orders', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('Orders API response status:', response.status);
            if (!response.ok) {
                throw new Error(`Failed to load orders: ${response.status}`);
            }
            return response.json();
        })
        .then(orders => {
            console.log('Received orders data for assignments:', orders);
            
            assignmentsLoadingElement.style.display = 'none';
            
            // Filter only active orders (not delivered or cancelled)
            const activeOrders = orders.filter(order => 
                order.status && 
                order.status.statusName !== 'DELIVERED' && 
                order.status.statusName !== 'CANCELLED'
            );
            
            console.log('Active orders:', activeOrders);
            
            if (!activeOrders || activeOrders.length === 0) {
                noAssignmentsElement.style.display = 'block';
                assignmentsListElement.style.display = 'none';
                return;
            }
            
            assignmentsListElement.innerHTML = '';
            assignmentsListElement.style.display = 'block';
            
            activeOrders.forEach(order => {
                const assignmentItem = document.createElement('div');
                assignmentItem.className = 'assignment-item';
                
                // Format date
                const createdDate = new Date(order.createdAt);
                const formattedDate = createdDate.toLocaleDateString();
                
                // Get customer name safely
                const customerName = order.customer ? 
                    `${order.customer.firstName || ''} ${order.customer.lastName || ''}`.trim() : 
                    'Unknown';
                
                assignmentItem.innerHTML = `
                    <div class="assignment-header">
                        <span class="assignment-id">Order #${order.id}</span>
                        <span class="assignment-status">${order.status ? order.status.statusName : 'Unknown'}</span>
                    </div>
                    <div class="assignment-details">
                        <p><strong>Customer:</strong> ${customerName}</p>
                        <p><strong>Created:</strong> ${formattedDate}</p>
                        <p><strong>Route:</strong> ${order.startLocation || 'N/A'} → ${order.endLocation || 'N/A'}</p>
                    </div>
                    <div class="assignment-actions">
                        <a href="/driver/orders/${order.id}" class="btn btn-primary">View Details</a>
                    </div>
                `;
                
                assignmentsListElement.appendChild(assignmentItem);
            });
        })
        .catch(error => {
            console.error('Error loading assignments:', error);
            assignmentsLoadingElement.style.display = 'none';
            assignmentsListElement.innerHTML = `
                <div class="alert alert-danger">
                    Error loading assignments: ${error.message}
                    <br>
                    <button class="btn btn-sm btn-primary mt-2" onclick="loadDriverAssignments()">Try Again</button>
                </div>
            `;
            assignmentsListElement.style.display = 'block';
        });
}

// Load order details
function loadOrderDetails(orderId) {
    const orderDetailsContainer = document.getElementById('order-details');
    const loadingElement = document.getElementById('loading');
    if (!orderDetailsContainer || !loadingElement) return;
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        loadingElement.innerHTML = `
            <div class="alert alert-danger">
                Authentication error. Please log in again.
            </div>
        `;
        return;
    }
    
    // Fetch order details from API
    fetch(`/api/driver/orders/${orderId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('API response status:', response.status);
            if (!response.ok) {
                throw new Error(`Failed to load order details: ${response.status}`);
            }
            return response.json();
        })
        .then(order => {
            console.log('Received order data:', order);
            
            // Populate order details
            document.getElementById('order-id').textContent = order.id;
            document.getElementById('order-status').textContent = order.status ? order.status.statusName : 'Unknown';
            document.getElementById('order-status').className = `status-${order.status ? order.status.statusName.toLowerCase() : 'unknown'}`;
            
            document.getElementById('order-created').textContent = formatDate(order.createdAt);
            document.getElementById('order-customer').textContent = order.customer ? 
                `${order.customer.firstName} ${order.customer.lastName}` : 'Unknown';
            
            // Load route and cargo info
            loadRouteInfo(orderId);
            loadCargoInfo(orderId);
            
            // Show order details
            loadingElement.style.display = 'none';
            orderDetailsContainer.style.display = 'block';
        })
        .catch(error => {
            console.error('Error:', error);
            loadingElement.innerHTML = `
                <div class="alert alert-danger">
                    Failed to load order details. Please try again later.
                    <br>
                    <small>Error details: ${error.message}</small>
                    <br>
                    <button class="btn btn-sm btn-primary mt-2" onclick="loadOrderDetails(${orderId})">Try Again</button>
                </div>
            `;
        });
}

// Load route information
function loadRouteInfo(orderId) {
    const routeInfoElement = document.getElementById('route-info');
    if (!routeInfoElement) return;
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    if (!token) return;
    
    // Fetch route info from API
    fetch(`/api/orders/${orderId}/route`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('Route API response status:', response.status);
            if (!response.ok) {
                throw new Error(`Failed to load route information: ${response.status}`);
            }
            return response.json();
        })
        .then(route => {
            console.log('Received route data:', route);
            
            // Populate route info
            document.getElementById('route-start').textContent = route.startLocation || 'N/A';
            document.getElementById('route-end').textContent = route.endLocation || 'N/A';
            
            // Initialize map if needed
            if (typeof initMap === 'function') {
                initMap();
            }
        })
        .catch(error => {
            console.error('Error loading route information:', error);
            routeInfoElement.innerHTML = `
                <div class="alert alert-danger">
                    Error loading route information.
                    <br>
                    <small>Error details: ${error.message}</small>
                    <br>
                    <button class="btn btn-sm btn-primary mt-2" onclick="loadRouteInfo(${orderId})">Try Again</button>
                </div>
            `;
        });
}

// Load cargo information
function loadCargoInfo(orderId) {
    const cargoInfoElement = document.getElementById('cargo-info');
    if (!cargoInfoElement) return;
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    if (!token) return;
    
    // Fetch cargo info from API
    fetch(`/api/orders/${orderId}/cargo`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('Cargo API response status:', response.status);
            if (!response.ok) {
                throw new Error(`Failed to load cargo information: ${response.status}`);
            }
            return response.json();
        })
        .then(cargo => {
            console.log('Received cargo data:', cargo);
            
            // Populate cargo info
            document.getElementById('cargo-type').textContent = cargo.type || 'N/A';
            document.getElementById('cargo-weight').textContent = cargo.weight || 'N/A';
        })
        .catch(error => {
            console.error('Error loading cargo information:', error);
            cargoInfoElement.innerHTML = `
                <div class="alert alert-danger">
                    Error loading cargo information.
                    <br>
                    <small>Error details: ${error.message}</small>
                    <br>
                    <button class="btn btn-sm btn-primary mt-2" onclick="loadCargoInfo(${orderId})">Try Again</button>
                </div>
            `;
        });
}

// Load location history
function loadLocationHistory(orderId) {
    const locationLoadingElement = document.getElementById('location-loading');
    const noLocationsElement = document.getElementById('no-locations');
    const locationListElement = document.getElementById('location-list');
    
    if (!locationLoadingElement || !noLocationsElement || !locationListElement) return;
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    if (!token) return;
    
    console.log('Loading location history for order:', orderId);
    
    // Fetch location history from API
    fetch(`/api/driver/orders/${orderId}/location/history`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('Location history API response status:', response.status);
            if (!response.ok) {
                throw new Error(`Failed to load location history: ${response.status}`);
            }
            return response.json();
        })
        .then(locations => {
            console.log('Received location history data:', locations);
            
            locationLoadingElement.style.display = 'none';
            
            if (!locations || locations.length === 0) {
                noLocationsElement.style.display = 'block';
                locationListElement.style.display = 'none';
                return;
            }
            
            locationListElement.innerHTML = '';
            locationListElement.style.display = 'block';
            
            locations.forEach(location => {
                const timestamp = new Date(location.timestamp);
                const formattedTime = timestamp.toLocaleDateString() + ' ' + timestamp.toLocaleTimeString();
                
                const locationItem = document.createElement('div');
                locationItem.className = 'location-item';
                locationItem.innerHTML = `
                    <div class="location-time">${formattedTime}</div>
                    <div class="location-coords">
                        <span>Lat: ${location.latitude.toFixed(6)}</span>
                        <span>Lng: ${location.longitude.toFixed(6)}</span>
                    </div>
                    ${location.statusComment ? `<div class="location-comment">${location.statusComment}</div>` : ''}
                `;
                
                locationListElement.appendChild(locationItem);
                
                // Add marker to map if map exists
                if (typeof map !== 'undefined' && map) {
                    L.marker([location.latitude, location.longitude])
                        .addTo(map)
                        .bindPopup(`${formattedTime}<br>${location.statusComment || ''}`);
                }
            });
            
            // Center map on the latest location if map exists
            if (typeof map !== 'undefined' && map && locations.length > 0) {
                const latest = locations[0]; // Assuming sorted by timestamp desc
                map.setView([latest.latitude, latest.longitude], 13);
            }
        })
        .catch(error => {
            console.error('Error loading location history:', error);
            locationLoadingElement.innerHTML = `
                <div class="alert alert-danger">
                    Error loading location history.
                    <br>
                    <small>Error details: ${error.message}</small>
                    <br>
                    <button class="btn btn-sm btn-primary mt-2" onclick="loadLocationHistory(${orderId})">Try Again</button>
                </div>
            `;
        });
}

// Load available statuses
function loadAvailableStatuses() {
    const statusSelect = document.getElementById('status-select');
    if (!statusSelect) return;
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    if (!token) return;
    
    console.log('Loading available statuses');
    
    // Fetch available statuses from API
    fetch('/api/driver/orders/statuses', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('Statuses API response status:', response.status);
            if (!response.ok) {
                throw new Error(`Failed to load available statuses: ${response.status}`);
            }
            return response.json();
        })
        .then(statuses => {
            console.log('Received statuses data:', statuses);
            
            statusSelect.innerHTML = '';
            
            statuses.forEach(status => {
                const option = document.createElement('option');
                option.value = status.id;
                option.textContent = status.statusName;
                statusSelect.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error loading available statuses:', error);
            statusSelect.innerHTML = '<option value="">Error loading statuses</option>';
        });
}

// Update order status
function updateOrderStatus() {
    const statusId = document.getElementById('status-select').value;
    const statusComment = document.getElementById('status-comment').value;
    const useCurrentLocation = document.getElementById('use-current-location').checked;
    
    if (!statusId) {
        alert('Please select a status');
        return;
    }
    
    // Get order ID from URL
    const pathParts = window.location.pathname.split('/');
    const orderId = pathParts[pathParts.length - 1];
    
    let updateData = {
        statusId: parseInt(statusId),
        statusComment: statusComment
    };
    
    if (useCurrentLocation && currentPosition) {
        updateData.latitude = currentPosition.latitude;
        updateData.longitude = currentPosition.longitude;
    }
    
    // Disable button during update
    const updateButton = document.getElementById('update-status-btn');
    updateButton.disabled = true;
    updateButton.textContent = 'Updating...';
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        alert('Authentication error. Please log in again.');
        updateButton.disabled = false;
        updateButton.textContent = 'Update Status';
        return;
    }
    
    console.log('Updating order status:', updateData);
    
    // Send API request
    fetch(`/api/driver/orders/${orderId}/status`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(updateData)
    })
        .then(response => {
            console.log('Update status API response status:', response.status);
            if (!response.ok) {
                throw new Error(`Failed to update order status: ${response.status}`);
            }
            return response.json();
        })
        .then(updatedOrder => {
            console.log('Received updated order data:', updatedOrder);
            
            // Update status display
            document.getElementById('order-status').textContent = updatedOrder.status.statusName;
            document.getElementById('order-status').className = `status-${updatedOrder.status.statusName.toLowerCase()}`;
            
            // Clear comment field
            document.getElementById('status-comment').value = '';
            
            // Reload location history
            loadLocationHistory(orderId);
            
            // Re-enable button
            updateButton.disabled = false;
            updateButton.textContent = 'Update Status';
            
            alert('Order status updated successfully');
        })
        .catch(error => {
            console.error('Error updating order status:', error);
            alert('Error updating order status. Please try again.');
            
            // Re-enable button
            updateButton.disabled = false;
            updateButton.textContent = 'Update Status';
        });
}

// Initialize map
function initMap() {
    const mapElement = document.getElementById('map');
    if (!mapElement) return;
    
    console.log('Initializing map');
    
    map = L.map('map').setView([50.4501, 30.5234], 13); // Default view (Kyiv)
    
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);
    
    // Get order ID from URL
    const pathParts = window.location.pathname.split('/');
    const orderId = pathParts[pathParts.length - 1];
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    if (!token) return;
    
    // Load latest location
    fetch(`/api/driver/orders/${orderId}/location`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('Latest location API response status:', response.status);
            if (!response.ok) {
                if (response.status === 404) {
                    console.log('No location found for order');
                    return null;
                }
                throw new Error(`Failed to load latest location: ${response.status}`);
            }
            return response.json();
        })
        .then(location => {
            if (location) {
                console.log('Received latest location data:', location);
                map.setView([location.latitude, location.longitude], 13);
                L.marker([location.latitude, location.longitude])
                    .addTo(map)
                    .bindPopup('Current location')
                    .openPopup();
            }
            
            // Load location history after map is initialized
            loadLocationHistory(orderId);
        })
        .catch(error => {
            console.error('Error loading latest location:', error);
        });
}

// Load vehicle details
function loadVehicleDetails() {
    const loadingElement = document.getElementById('loading');
    const vehicleDetailsElement = document.getElementById('vehicle-details');
    const errorMessageElement = document.getElementById('error-message');
    
    if (!loadingElement || !vehicleDetailsElement || !errorMessageElement) return;
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        loadingElement.style.display = 'none';
        errorMessageElement.textContent = 'Authentication error. Please log in again.';
        errorMessageElement.style.display = 'block';
        return;
    }
    
    console.log('Loading vehicle details');
    
    // Fetch vehicle details from API
    fetch('/api/driver/vehicle', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('Vehicle API response status:', response.status);
            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error('No vehicle assigned');
                }
                throw new Error(`Failed to load vehicle details: ${response.status}`);
            }
            return response.json();
        })
        .then(vehicle => {
            console.log('Received vehicle data:', vehicle);
            
            loadingElement.style.display = 'none';
            vehicleDetailsElement.style.display = 'block';
            
            // Populate vehicle details
            document.getElementById('license-plate').textContent = vehicle.licensePlate || 'N/A';
            document.getElementById('capacity').textContent = vehicle.capacity ? vehicle.capacity + ' kg' : 'N/A';
            
            // Set vehicle photo if available
            if (vehicle.photoUrl) {
                const img = new Image();
                img.onload = function() {
                    document.getElementById('vehicle-photo').src = vehicle.photoUrl;
                };
                img.onerror = function() {
                    console.log('Error loading vehicle photo from URL:', vehicle.photoUrl);
                    document.getElementById('vehicle-photo').src = '/images/default-truck.jpg';
                };
                img.src = vehicle.photoUrl;
            } else {
                console.log('No photo URL provided, using default truck image');
            }
        })
        .catch(error => {
            console.error('Error loading vehicle details:', error);
            loadingElement.style.display = 'none';
            
            if (error.message === 'No vehicle assigned') {
                errorMessageElement.textContent = 'No vehicle is currently assigned to you. Please contact your manager.';
            } else {
                errorMessageElement.textContent = `Error loading vehicle details: ${error.message}. Please try again.`;
            }
            errorMessageElement.style.display = 'block';
        });
}

// Format date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
}

// Initialize driver page
document.addEventListener('DOMContentLoaded', function() {
    console.log('Driver page initialized');
    
    // Check if user is authenticated
    if (!localStorage.getItem('jwt_token')) {
        console.log('User not authenticated, redirecting to login');
        window.location.href = '/login';
        return;
    }
    
    // Load driver orders if on orders page
    if (window.location.pathname === '/driver/orders') {
        console.log('On orders page, loading driver orders');
        loadDriverOrders();
    }
    
    // Load order details if on order details page
    const orderDetailsMatch = window.location.pathname.match(/\/driver\/orders\/(\d+)/);
    if (orderDetailsMatch) {
        const orderId = orderDetailsMatch[1];
        console.log('On order details page, loading order:', orderId);
        loadOrderDetails(orderId);
    }
    
    // Load vehicle details if on vehicle page
    if (window.location.pathname === '/driver/vehicle') {
        console.log('On vehicle page, loading vehicle details');
        loadVehicleDetails();
        loadDriverAssignments();
    }
});
