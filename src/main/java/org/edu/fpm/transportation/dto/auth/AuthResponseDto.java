package org.edu.fpm.transportation.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String role;
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
}
