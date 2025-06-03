/**
 * Driver related JavaScript functions
 */

// Load driver orders
function loadDriverOrders() {
    const ordersContainer = document.getElementById('ordersContainer');
    if (!ordersContainer) return;
    
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
});
