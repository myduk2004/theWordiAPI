package theWordI.backend.config;

import com.sun.net.httpserver.HttpsServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import theWordI.backend.domain.user.exception.AccountLockedException;
import theWordI.backend.exception.UnAuthorizedException;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<?> handleAccountLocked(AccountLockedException e)
    {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }

    //403 : 당신이 누구인지는 알겠는데, 이 작업에 대한 권한이 없다
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        String message = (e.getMessage() == null || e.getMessage().isBlank())
                ? "해당 작업에 대한 권한이 없습니다."
                : e.getMessage();
        ErrorResponse response = new ErrorResponse("FORBIDDEN", message);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    //401 : 당신이 누구인지 모르겠다
    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorizedException(UnAuthorizedException e) {

        ErrorResponse response = new ErrorResponse("UNAUTHORIZED", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("잘못된 요청입니다.");
    }


    //파라미터 누락 요청 시 400로 처리(토큰 refresh처리 안하기 위해)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleRuntimeException(Exception  ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage() + "123");
    }

    //@Valid 검증 실패 시 발생하는 예외를 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex)
    {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

       return ResponseEntity.badRequest().body(errors);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e)
    {
        ErrorResponse response = new ErrorResponse("INVALID_INPUT", e.getLocalizedMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    record  ErrorResponse(String code, String message){}
}
