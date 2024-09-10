package duongvanbao.Book.Store.config.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import duongvanbao.Book.Store.dto.auth.AuthResponse;
import duongvanbao.Book.Store.dto.auth.GoogleUser;
import duongvanbao.Book.Store.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.ObjectProvider;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ObjectProvider<AuthService> authServiceProvider;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(ObjectProvider<AuthService> authServiceProvider) {
        this.authServiceProvider = authServiceProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String sub = oauth2User.getAttribute("sub");
        String name = oauth2User.getAttribute("name");
        String givenName = oauth2User.getAttribute("given_name");
        String familyName = oauth2User.getAttribute("family_name");
        String picture = oauth2User.getAttribute("picture");
        String email = oauth2User.getAttribute("email");
        boolean emailVerified = Boolean.TRUE.equals(oauth2User.getAttribute("email_verified"));
        GoogleUser googleUser = new GoogleUser(
                sub,
                name,
                givenName,
                familyName,
                picture,
                email,
                emailVerified
        );
        try {
            AuthService authService = authServiceProvider.getObject();
            AuthResponse authResponse = authService.authenticateGoogle(googleUser);
            response.setContentType("application/json");
//            response.getWriter().print(new ObjectMapper().writeValueAsString(authResponse));
            String encodedToken = URLEncoder.encode(authResponse.token(), StandardCharsets.UTF_8);
            response.sendRedirect(System.getenv("GOOGLE_REDIRECT_URI")+"/"+encodedToken);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write(e.getMessage());
        }
    }
}