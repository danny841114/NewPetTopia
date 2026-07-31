package petTopia.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import petTopia.dto.exception.CustomErrorResponse;
import petTopia.exception.custom.AlreadyReviewedException;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("RESOURCE NOT FOUND", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code("RESOURCE_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("INVALID PARAMETER", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code("INVALID_PARAMETER")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.error("BAD CREDENTIAL", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .code("BAD_CREDENTIAL")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleUserNotFound(UsernameNotFoundException ex) {
        log.error("USER NOT FOUND", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .code("USER_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomErrorResponse> handleAuthorization(AccessDeniedException ex) {
        log.error("FORBIDDEN", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .code("FORBIDDEN")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomErrorResponse> handleRuntimeError(RuntimeException ex) {
        log.error("SYSTEM ERROR", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code("SYSTEM_ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(AlreadyReviewedException.class)
    public ResponseEntity<CustomErrorResponse> handleAlreadyReview(AlreadyReviewedException ex) {
        log.error("ALREADY REVIEW", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code("ALREADY REVIEW")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.internalServerError().body(response);
    }
}
