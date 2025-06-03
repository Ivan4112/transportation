/**
 * Customer related JavaScript functions
 */

// Load customer orders
function loadCustomerOrders() {
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
    
    console.log('Fetching orders with token:', token);
    
    // Direct fetch with token
    fetch('/api/customer/orders', {
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
                        You don't have any orders yet. <a href="/customer/orders/create">Create your first order</a>.
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
                            <th>Price</th>
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
                        <td>${formatCurrency(order.price)}</td>
                        <td>${formatDate(order.createdAt)}</td>
                        <td>
                            <a href="/customer/orders/${order.id}" class="btn btn-info btn-sm">Details</a>
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
                    <button class="btn btn-sm btn-primary mt-2" onclick="loadCustomerOrders()">Try Again</button>
                </div>
            `;
        });
}

// Load order details
function loadOrderDetails(orderId) {
    const orderDetailsContainer = document.getElementById('orderDetailsContainer');
    if (!orderDetailsContainer) return;
    
    // Show loading indicator
    orderDetailsContainer.innerHTML = '<div class="text-center"><p>Loading order details...</p></div>';
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        orderDetailsContainer.innerHTML = `
            <div class="alert alert-danger">
                Authentication error. Please log in again.
            </div>
        `;
        return;
    }
    
    // Fetch order details from API
    fetch(`/api/customer/orders/${orderId}`, {
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
            
            // Create order details HTML
            let html = `
                <div class="row" style="display: flex; flex-wrap: wrap; margin: -10px;">
                    <!-- Order Status -->
                    <div style="flex: 1; min-width: 300px; padding: 10px;">
                        <div class="card">
                            <div class="card-header">
                                <h3>Status</h3>
                            </div>
                            <div class="card-body">
                                <div style="text-align: center; padding: 20px 0;">
                                    <span class="status-${order.status ? order.status.statusName.toLowerCase() : 'unknown'}" style="font-size: 1.5rem; padding: 10px 20px;">${order.status ? order.status.statusName : 'Unknown'}</span>
                                </div>
                                <div style="margin-top: 15px;">
                                    <p><strong>Created:</strong> <span>${formatDate(order.createdAt)}</span></p>
                                    ${order.estimatedDeliveryTime ? `<p><strong>Estimated Delivery:</strong> <span>${formatDate(order.estimatedDeliveryTime)}</span></p>` : ''}
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Cargo Details -->
                    <div style="flex: 1; min-width: 300px; padding: 10px;">
                        <div class="card">
                            <div class="card-header">
                                <h3>Cargo Details</h3>
                            </div>
                            <div class="card-body">
                                <p><strong>Type:</strong> <span>${order.cargoType || 'N/A'}</span></p>
                                <p><strong>Weight:</strong> <span>${order.cargoWeight ? order.cargoWeight + ' kg' : 'N/A'}</span></p>
                                <p><strong>Price:</strong> <span>${formatCurrency(order.price)}</span></p>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Route Information -->
                    <div style="flex: 1; min-width: 300px; padding: 10px;">
                        <div class="card">
                            <div class="card-header">
                                <h3>Route Information</h3>
                            </div>
                            <div class="card-body">
                                <p><strong>From:</strong> <span>${order.startLocation || 'N/A'}</span></p>
                                <p><strong>To:</strong> <span>${order.endLocation || 'N/A'}</span></p>
                                <p><strong>Distance:</strong> <span>${order.distance ? order.distance + ' km' : 'N/A'}</span></p>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Driver Information -->
                    <div style="flex: 1; min-width: 300px; padding: 10px;">
                        <div class="card">
                            <div class="card-header">
                                <h3>Driver Information</h3>
                            </div>
                            <div class="card-body">
                                ${order.driverName ? `
                                    <p><strong>Driver:</strong> <span>${order.driverName}</span></p>
                                    ${order.vehicleLicensePlate ? `<p><strong>Vehicle:</strong> <span>${order.vehicleLicensePlate}</span></p>` : ''}
                                ` : `
                                    <div style="text-align: center; padding: 20px 0;">
                                        <p>No driver assigned yet</p>
                                    </div>
                                `}
                            </div>
                        </div>
                    </div>
                </div>
            `;
            
            // Add tracking map if order is in transit
            if (order.status && order.status.statusName === 'IN_TRANSIT') {
                html += `
                    <div class="card mt-3">
                        <div class="card-header">
                            <h3>Live Tracking</h3>
                        </div>
                        <div class="card-body">
                            <div id="map" style="height: 400px; width: 100%;"></div>
                        </div>
                    </div>
                `;
            }
            
            orderDetailsContainer.innerHTML = html;
            
            // Initialize map if order is in transit
            if (order.status && order.status.statusName === 'IN_TRANSIT') {
                initMap(orderId);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            orderDetailsContainer.innerHTML = `
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

// Calculate order price
function calculateOrderPrice() {
    const cargoType = document.getElementById('cargoType').value;
    const cargoWeight = document.getElementById('cargoWeight').value;
    const startLocation = document.getElementById('startLocation').value;
    const endLocation = document.getElementById('endLocation').value;
    
    // Validate form
    if (!cargoType || !cargoWeight || !startLocation || !endLocation) {
        alert('Please fill in all fields');
        return;
    }
    
    // Create request body
    const requestBody = {
        cargoType: cargoType,
        cargoWeight: parseFloat(cargoWeight),
        startLocation: startLocation,
        endLocation: endLocation
    };
    
    // Show loading indicator
    const calculateButton = document.getElementById('calculatePriceButton');
    const originalButtonText = calculateButton.textContent;
    calculateButton.disabled = true;
    calculateButton.textContent = 'Calculating...';
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    
    // Send API request
    fetch('/api/customer/orders/calculate-price', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to calculate price');
        }
        return response.json();
    })
    .then(data => {
        // Update price preview
        document.getElementById('previewCargoType').textContent = data.cargoType;
        document.getElementById('previewWeight').textContent = data.cargoWeight.toLocaleString() + ' kg';
        document.getElementById('previewDistance').textContent = data.distance + ' km';
        document.getElementById('previewRoute').textContent = data.startLocation + ' → ' + data.endLocation;
        document.getElementById('previewPrice').textContent = formatCurrency(data.price);
        
        // Show price preview
        document.getElementById('pricePreview').style.display = 'block';
        
        // Reset button
        calculateButton.disabled = false;
        calculateButton.textContent = originalButtonText;
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to calculate price. Please try again.');
        
        // Reset button
        calculateButton.disabled = false;
        calculateButton.textContent = originalButtonText;
    });
}

// Create new order
function createOrder(event) {
    event.preventDefault();
    
    const cargoType = document.getElementById('cargoType').value;
    const cargoWeight = document.getElementById('cargoWeight').value;
    const startLocation = document.getElementById('startLocation').value;
    const endLocation = document.getElementById('endLocation').value;
    
    // Validate form
    if (!cargoType || !cargoWeight || !startLocation || !endLocation) {
        alert('Please fill in all fields');
        return;
    }
    
    // Create request body
    const requestBody = {
        cargoType: cargoType,
        cargoWeight: parseFloat(cargoWeight),
        startLocation: startLocation,
        endLocation: endLocation
    };
    
    // Show loading indicator
    const submitButton = document.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = 'Creating Order...';
    
    // Get token directly
    const token = localStorage.getItem('jwt_token');
    
    // Send API request
    fetch('/api/customer/orders/create', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to create order');
        }
        return response.json();
    })
    .then(data => {
        // Redirect to order details page
        window.location.href = `/customer/orders/${data.id}?success=true`;
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to create order. Please try again.');
        
        // Reset button
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
    });
}

// Initialize map for order tracking
function initMap(orderId) {
    // In a real application, you would use a mapping library like Leaflet or Google Maps
    console.log("Map initialized for order:", orderId);
    
    // Fetch initial location
    updateLocation(orderId);
    
    // Poll for location updates every 30 seconds
    setInterval(() => updateLocation(orderId), 30000);
}

// Update order location on map
function updateLocation(orderId) {
    const token = localStorage.getItem('jwt_token');
    
    fetch(`/api/tracking/orders/${orderId}/location`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch location');
        }
        return response.json();
    })
    .then(data => {
        // Update map with new location
        console.log("Location updated:", data);
        // In a real application, you would update the map marker position
    })
    .catch(error => {
        console.error('Error fetching location:', error);
    });
}

// Format date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
}

// Format currency
function formatCurrency(amount) {
    if (!amount && amount !== 0) return 'N/A';
    return new Intl.NumberFormat('uk-UA', { style: 'currency', currency: 'UAH' }).format(amount);
}

// Select cargo type
function selectCargoType(element) {
    // Remove selected class from all options
    document.querySelectorAll('.cargo-type-option').forEach(option => {
        option.classList.remove('selected');
    });
    
    // Add selected class to clicked option
    element.classList.add('selected');
    
    // Set the hidden input value
    document.getElementById('cargoType').value = element.getAttribute('data-value');
}

// Initialize customer page
document.addEventListener('DOMContentLoaded', function() {
    console.log('Customer page initialized');
    
    // Check if user is authenticated
    if (!localStorage.getItem('jwt_token')) {
        console.log('User not authenticated, redirecting to login');
        window.location.href = '/login';
        return;
    }
    
    // Load customer orders if on orders page
    if (window.location.pathname === '/customer/orders') {
        console.log('On orders page, loading customer orders');
        loadCustomerOrders();
    }
    
    // Load order details if on order details page
    const orderDetailsMatch = window.location.pathname.match(/\/customer\/orders\/(\d+)/);
    if (orderDetailsMatch) {
        const orderId = orderDetailsMatch[1];
        console.log('On order details page, loading order:', orderId);
        loadOrderDetails(orderId);
    }
    
    // Initialize order form if on create order page
    if (window.location.pathname === '/customer/orders/create') {
        console.log('On create order page, initializing form');
        
        // Set default cargo type
        const defaultCargoType = document.querySelector('.cargo-type-option[data-value="GRAIN"]');
        if (defaultCargoType) {
            selectCargoType(defaultCargoType);
        }
        
        // Add event listeners
        const calculateButton = document.getElementById('calculatePriceButton');
        if (calculateButton) {
            calculateButton.addEventListener('click', calculateOrderPrice);
        }
        
        const orderForm = document.getElementById('orderForm');
        if (orderForm) {
            orderForm.addEventListener('submit', createOrder);
        }
        
        // Add event listeners to cargo type options
        document.querySelectorAll('.cargo-type-option').forEach(option => {
            option.addEventListener('click', () => selectCargoType(option));
        });
    }
});
