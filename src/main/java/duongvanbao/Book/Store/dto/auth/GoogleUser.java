package duongvanbao.Book.Store.dto.auth;

public record GoogleUser(
        String sub,
        String name,
        String given_name,
        String family_name,
        String picture,
        String email,
        boolean email_verified
) {
}
