package petTopia.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import petTopia.dto.exception.CustomErrorResponse;
import petTopia.exception.custom.AlreadyReviewedException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
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

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalAlgorithm(NoSuchAlgorithmException ex) {
        log.error("NO SUCH ALGORITHM", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code("NO_SUCH_ALGORITHM")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UnsupportedEncodingException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalAlgorithm(UnsupportedEncodingException ex) {
        log.error("UNSUPPORTED ENCODING", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code("UNSUPPORTED_ENCODING")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ParseException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalAlgorithm(ParseException ex) {
        log.error("PARSE EXCEPTION", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code("PARSE_EXCEPTION")
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

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<CustomErrorResponse> handleDeniedAuthorization(AuthorizationDeniedException ex) {
        log.error("AUTHORIZATION DENIED", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .code("AUTHORIZATION_DENIED")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("ENTITY NOT FOUND", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .code("ENTITY_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

    @ExceptionHandler(IOException.class)
    public ResponseEntity<CustomErrorResponse> handleRuntimeError(IOException ex) {
        log.error("IO ERROR", ex);

        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code("IO_ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.internalServerError().body(response);
    }
}
