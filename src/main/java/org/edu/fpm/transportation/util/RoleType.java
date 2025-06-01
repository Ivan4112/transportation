package org.edu.fpm.transportation.util;

/**
 * Enum representing the available user roles in the system.
 */
public enum RoleType {
    CUSTOMER(Constants.CUSTOMER),
    DRIVER(Constants.DRIVER),
    SUPPORT_AGENT(Constants.SUPPORT_AGENT),
    ADMIN(Constants.ADMIN);

    private final String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    /**
     * Get RoleType enum from string role name
     * 
     * @param roleName String representation of role
     * @return RoleType enum value
     * @throws IllegalArgumentException if role name is not valid
     */
    public static RoleType fromString(String roleName) {
        for (RoleType roleType : RoleType.values()) {
            if (roleType.getRoleName().equalsIgnoreCase(roleName)) {
                return roleType;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + roleName);
    }
    
    /**
     * Constants for role names to be used in annotations and other string contexts
     */
    public static class Constants {
        public static final String CUSTOMER = "CUSTOMER";
        public static final String DRIVER = "DRIVER";
        public static final String SUPPORT_AGENT = "SUPPORT_AGENT";
        public static final String ADMIN = "ADMIN";
        
        private Constants() {
            // Private constructor to prevent instantiation
        }
    }
}
