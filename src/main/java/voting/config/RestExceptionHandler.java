package voting.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import voting.error.ErrorMessage;
import voting.error.ErrorType;
import voting.error.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@RestControllerAdvice
public class RestExceptionHandler {

    private final Logger log = getLogger(getClass());

    private static final Map<String, String> CONSTRAINTS = Map.of(
            "users_unique_email_idx", "User with this email already exists");

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    ErrorMessage notFound(NotFoundException e, HttpServletRequest request) {
        return assembleErrorMessage(e, request, ErrorType.DATA_NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InsufficientAuthenticationException.class)
    ErrorMessage auth(InsufficientAuthenticationException e, HttpServletRequest request) {
        return assembleErrorMessage(e, request, ErrorType.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    ErrorMessage conflict(DataIntegrityViolationException e, HttpServletRequest request) {
        String message = getRootCause(e).getLocalizedMessage().toLowerCase();
        for (Map.Entry<String, String> entry : CONSTRAINTS.entrySet()) {
            if (message.contains(entry.getKey())) {
                return assembleErrorMessage(e, request, ErrorType.DATA_ERROR, List.of(entry.getValue()));
            }
        }
        return assembleErrorMessage(e, request, ErrorType.DATA_ERROR);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(BindException.class)
    ErrorMessage bindException(BindException e, HttpServletRequest request) {
        return assembleErrorMessage(e, request, ErrorType.VALIDATION_ERROR, getErrorList(e.getBindingResult()));
    }

    private List<String> getErrorList(BindingResult result) {
        return result.getFieldErrors().stream()
                .map(fe -> String.format("[%s] %s", fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            HttpRequestMethodNotSupportedException.class,
            IllegalArgumentException.class,
            ConstraintViolationException.class,
            BadCredentialsException.class})
    public ErrorMessage badRequestException(Exception e, HttpServletRequest request) {
        return assembleErrorMessage(e, request, ErrorType.VALIDATION_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorMessage internalError(Exception e, HttpServletRequest request) {
        return assembleErrorMessage(e, request, ErrorType.APP_ERROR);
    }

    private ErrorMessage assembleErrorMessage(Exception e, HttpServletRequest request, ErrorType errorType, List<String> messages) {
        log.error("Exception raised: {}", e.getLocalizedMessage());
        return new ErrorMessage(
                LocalDateTime.now(),
                errorType,
                messages,
                request.getRequestURI());
    }

    private ErrorMessage assembleErrorMessage(Exception e, HttpServletRequest request, ErrorType errorType) {
        return assembleErrorMessage(e, request, errorType, Collections.singletonList(getRootCause(e).getLocalizedMessage()));
    }

    private Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }
}
