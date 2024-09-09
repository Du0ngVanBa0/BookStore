package duongvanbao.Book.Store.config.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import duongvanbao.Book.Store.dto.AuthResponse;
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
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        try {
            AuthService authService = authServiceProvider.getObject();
            AuthResponse authResponse = authService.authenticateGoogle(email, name);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(authResponse));
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write(e.getMessage());
        }
    }
}