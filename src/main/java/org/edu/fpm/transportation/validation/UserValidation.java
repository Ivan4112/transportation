package org.edu.fpm.transportation.validation;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.auth.signin.SignInRequestDto;
import org.edu.fpm.transportation.dto.auth.signup.SignUpRequestDto;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.exception.InvalidCredentialsForAuthException;
import org.edu.fpm.transportation.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.edu.fpm.transportation.util.ResourceConstants.*;


/**
 * Validation class for user-related data.
 * Contains methods to validate user input for registration and profile updates.
 */
@Component
@RequiredArgsConstructor
public class UserValidation {

    private final UserService userService;

    private final String FIELD_PASSWORD = "Password";

    // Regex pattern for Latin letters only
    private static final Pattern LATIN_LETTERS_PATTERN = Pattern.compile("^[a-zA-Z]+$");
    
    // Regex pattern for email validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // Regex patterns for password validation
    private static final Pattern HAS_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern HAS_LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern HAS_NUMBER = Pattern.compile(".*\\d.*");
    
    // Regex pattern for special characters in password
    // Includes: !@#$%^&*, but excludes ~ and \
    private static final Pattern HAS_SPECIAL_CHAR = Pattern.compile(".*[!@#$%^&*,].*");
    
    // Regex pattern to check for forbidden special characters
    private static final Pattern HAS_FORBIDDEN_CHAR = Pattern.compile(".*[~\\\\].*");

    /**
     * Validates if a string is not null or empty.
     *
     * @param value The string to validate
     * @param fieldName The name of the field for error message
     * @throws IllegalArgumentException If the string is null or empty
     */
    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new InvalidCredentialsForAuthException(fieldName + " is required");
        }
    }

    /**
     * Validates if a string contains only Latin letters.
     *
     * @param value The string to validate
     * @param fieldName The name of the field for error message
     * @throws IllegalArgumentException If the string contains non-Latin characters
     */
    private void validateLatinLetters(String value, String fieldName) {
        validateRequired(value, fieldName);
        if (!LATIN_LETTERS_PATTERN.matcher(value).matches()) {
            throw new InvalidCredentialsForAuthException(MESSAGE_ONLY_LATIN_LETTER_ALLOWED);
        }
    }

    private void validateNameLength(String value, String fieldName) {
        validateRequired(value, fieldName);
        if (value.length() > 50 || value.length() < 2) {
            throw new InvalidCredentialsForAuthException(fieldName + " should be longer than 2 and less than 50 characters");
        }
    }
    /**
     * Validates if an email has a valid format.
     *
     * @param email The email to validate
     * @throws IllegalArgumentException If the email format is invalid
     */
    private void validateEmailFormat(String email) {
        String FIELD_EMAIL = "Email";
        validateRequired(email, FIELD_EMAIL);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidCredentialsForAuthException(MESSAGE_EMAIL_NOT_VALID);
        }
    }

    /**
     * Validates if an email is not already registered.
     *
     * @param email The email to validate
     * @throws IllegalArgumentException If the email is already registered
     */
    private void validateEmailNotRegistered(String email) {
        validateEmailFormat(email);
        Optional<User> existingUser = Optional.ofNullable(userService.getUserByEmail(email));
        if (existingUser.isPresent()) {
            throw new InvalidCredentialsForAuthException(MESSAGE_EMAIL_ALREADY_IN_USE);
        }
    }

    /**
     * Validates if a password meets the minimum length requirement.
     *
     * @param password The password to validate
     * @throws IllegalArgumentException If the password is too short
     */
    private void validatePasswordLength(String password) {
        validateRequired(password, FIELD_PASSWORD);
        if (password.length() < 8 || password.length() > 16) {
            throw new InvalidCredentialsForAuthException(MESSAGE_PASSWORD_SHOULD_BE_LONGER);
        }
    }

    /**
     * Validates if a password contains at least one uppercase letter.
     *
     * @param password The password to validate
     * @throws IllegalArgumentException If the password doesn't contain an uppercase letter
     */
    private void validatePasswordUppercase(String password) {
        validateRequired(password, FIELD_PASSWORD);
        if (!HAS_UPPERCASE.matcher(password).matches()) {
            throw new InvalidCredentialsForAuthException(MESSAGE_PASSWORD_SHOULD_CONTAIN_UPPER_LETTER);
        }
    }

    /**
     * Validates if a password contains at least one lowercase letter.
     *
     * @param password The password to validate
     * @throws IllegalArgumentException If the password doesn't contain a lowercase letter
     */
    private void validatePasswordLowercase(String password) {
        validateRequired(password, FIELD_PASSWORD);
        if (!HAS_LOWERCASE.matcher(password).matches()) {
            throw new InvalidCredentialsForAuthException(MESSAGE_PASSWORD_SHOULD_CONTAIN_LOWER_LETTER);
        }
    }

    /**
     * Validates if a password contains at least one number.
     *
     * @param password The password to validate
     * @throws IllegalArgumentException If the password doesn't contain a number
     */
    private void validatePasswordNumber(String password) {
        validateRequired(password, FIELD_PASSWORD);
        if (!HAS_NUMBER.matcher(password).matches()) {
            throw new InvalidCredentialsForAuthException(MESSAGE_PASSWORD_SHOULD_CONTAIN_NUMBER);
        }
    }
    
    /**
     * Validates if a password contains at least one allowed special character.
     *
     * @param password The password to validate
     * @throws IllegalArgumentException If the password doesn't contain an allowed special character
     */
    private void validatePasswordSpecialChar(String password) {
        validateRequired(password, FIELD_PASSWORD);
        if (HAS_FORBIDDEN_CHAR.matcher(password).matches()) {
            throw new InvalidCredentialsForAuthException(MESSAGE_PASSWORD_SHOULD_NOT_CONTAIN_SUCH_CHAR);
        }

        if (!HAS_SPECIAL_CHAR.matcher(password).matches()) {
            throw new InvalidCredentialsForAuthException(MESSAGE_PASSWORD_SHOULD_CONTAIN_SPECIAL_CHAR);
        }
    }

    /**
     * Validates all password requirements.
     *
     * @param password The password to validate
     * @throws IllegalArgumentException If the password doesn't meet any requirement
     */
    public void validatePassword(String password) {
        validatePasswordLength(password);
        validatePasswordUppercase(password);
        validatePasswordLowercase(password);
        validatePasswordNumber(password);
        validatePasswordSpecialChar(password);
    }

    /**
     * Validates the login request.
     *
     * @param request The login request to validate
     * @throws IllegalArgumentException If validation fails
     */
    public void validateLoginRequest(SignInRequestDto request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new InvalidCredentialsForAuthException(MESSAGE_EMAIL_REQUIRED);
        }

        validateEmailFormat(request.getEmail());

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new InvalidCredentialsForAuthException(MESSAGE_PASSWORD_REQUIRED);
        }
    }


    /**
     * Validates all user registration data.
     *
     * @param request The data to validate
     * @throws IllegalArgumentException If any validation fails
     */
    public void validateUserRegistration(SignUpRequestDto request) {
        String FIELD_FIRST_NAME = "First name";
        String FIELD_LAST_NAME = "Last name";
        validateEmailNotRegistered(request.getEmail());
        validateNameLength(request.getFirstName(), FIELD_FIRST_NAME);
        validateNameLength(request.getLastName(), FIELD_LAST_NAME);
        validateLatinLetters(request.getFirstName(), FIELD_FIRST_NAME);
        validateLatinLetters(request.getLastName(), FIELD_LAST_NAME);
        validatePassword(request.getPassword());
    }
}
