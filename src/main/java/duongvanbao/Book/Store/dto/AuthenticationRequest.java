package duongvanbao.Book.Store.dto;

public record AuthenticationRequest(
        String email,
        String password
) {
}
