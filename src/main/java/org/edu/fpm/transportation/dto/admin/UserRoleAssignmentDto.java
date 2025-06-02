package org.edu.fpm.transportation.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for assigning roles to users by email
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleAssignmentDto {
    private String email;
    private String roleName;
}
