package duongvanbao.Book.Store.exception;

import duongvanbao.Book.Store.dto.utils.errorResponse.ErrorResponse;
import duongvanbao.Book.Store.dto.utils.errorResponse.ErrorResponseWithData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ErrorResponseWithData<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ErrorResponseWithData<>(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ErrorResponse handleRunTimeExceptions(Exception ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ErrorResponse handleAllExceptions(Exception ex) {
        return new ErrorResponse("Đã xảy ra lỗi!");
    }
}
