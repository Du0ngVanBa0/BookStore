package duongvanbao.Book.Store.dto.utils.errorResponse;

import lombok.Getter;

@Getter
public class ErrorResponseWithData<T> {
    private T data;
    private boolean success = false;

    public ErrorResponseWithData(T data) {
        this.data = data;
    }
}
