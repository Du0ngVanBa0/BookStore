package duongvanbao.Book.Store.dto.utils.successResponse;

import lombok.Getter;

@Getter
public class SuccessResponseWithoutData {
    private String message;
    private final boolean success = true;

    public SuccessResponseWithoutData(String message) {
        this.message = message;
    }
}
