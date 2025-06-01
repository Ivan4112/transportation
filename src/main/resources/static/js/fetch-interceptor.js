/**
 * Interceptor for automatically adding JWT token to all fetch requests and XMLHttpRequest
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('Initializing fetch and XHR interceptors');
    
    // Intercept all fetch requests
    const originalFetch = window.fetch;
    
    window.fetch = function(url, options = {}) {
        // Get JWT token from localStorage
        const token = localStorage.getItem('jwt_token');
        
        // If token exists, add it to headers
        if (token) {
            options = options || {};
            options.headers = options.headers || {};
            options.headers['Authorization'] = `Bearer ${token}`;
        }
        
        return originalFetch(url, options);
    };
    
    // Intercept all XMLHttpRequest
    (function() {
        const originalOpen = XMLHttpRequest.prototype.open;
        
        XMLHttpRequest.prototype.open = function() {
            const token = localStorage.getItem('jwt_token');
            
            originalOpen.apply(this, arguments);
            
            if (token) {
                this.setRequestHeader('Authorization', `Bearer ${token}`);
            }
        };
    })();
    
    console.log('Fetch and XHR interceptors installed');
});
