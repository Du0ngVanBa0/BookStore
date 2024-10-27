package duongvanbao.Book.Store.dto.utils.successResponse;

import lombok.Getter;

@Getter
public class SuccessResponseWithData<T> {
    private T data;
    private final boolean success = true;

    public SuccessResponseWithData(T data) {
        this.data = data;
    }
}
