package org.edu.fpm.transportation.dto.auth.signin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequestDto {
    private String email;
    private String password;
}
