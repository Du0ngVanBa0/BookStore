package duongvanbao.Book.Store.dto.auth;

public record AuthenticationRequest(
        String email,
        String password
) {
}
