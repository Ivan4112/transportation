package org.edu.fpm.transportation.util;

/**
 * Enum representing the available order statuses in the system.
 */
public enum OrderStatusType {
    PENDING(Constants.PENDING),
    ASSIGNED(Constants.ASSIGNED),
    IN_TRANSIT(Constants.IN_TRANSIT),
    WAITING_UNLOADING(Constants.WAITING_UNLOADING),
    DELIVERED(Constants.DELIVERED),
    CANCELLED(Constants.CANCELLED);

    private final String statusName;

    OrderStatusType(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    /**
     * Get OrderStatusType enum from string status name
     * 
     * @param statusName String representation of status
     * @return OrderStatusType enum value
     * @throws IllegalArgumentException if status name is not valid
     */
    public static OrderStatusType fromString(String statusName) {
        for (OrderStatusType statusType : OrderStatusType.values()) {
            if (statusType.getStatusName().equalsIgnoreCase(statusName)) {
                return statusType;
            }
        }
        throw new IllegalArgumentException("Unknown order status: " + statusName);
    }
    
    /**
     * Constants for status names to be used in annotations and other string contexts
     */
    public static class Constants {
        public static final String PENDING = "PENDING";
        public static final String ASSIGNED = "ASSIGNED";
        public static final String IN_TRANSIT = "IN_TRANSIT";
        public static final String WAITING_UNLOADING = "WAITING_UNLOADING";
        public static final String DELIVERED = "DELIVERED";
        public static final String CANCELLED = "CANCELLED";
        
        private Constants() {
            // Private constructor to prevent instantiation
        }
    }
}
