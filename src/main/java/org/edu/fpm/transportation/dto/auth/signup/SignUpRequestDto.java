package org.edu.fpm.transportation.dto.auth.signup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
