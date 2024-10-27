package duongvanbao.Book.Store.dto.utils.errorResponse;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String message;
    private boolean success = false;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
