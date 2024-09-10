package duongvanbao.Book.Store.service;

import duongvanbao.Book.Store.config.JwtService;
import duongvanbao.Book.Store.dto.auth.*;
import duongvanbao.Book.Store.model.Role;
import duongvanbao.Book.Store.model.User;
import duongvanbao.Book.Store.model.UserTicket;
import duongvanbao.Book.Store.utils.Util;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTicketService userTicketService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public MessageResponse register(RegisterRequest data) throws Exception {
        String otp = Util.generateOtp();
        var user = User.builder()
                .name(data.name())
                .email(data.email())
                .password(passwordEncoder.encode(data.password()))
                .role(Role.USER)
                .enabled(false)
                .build();
        var ticket = UserTicket.builder()
                .email(data.email())
                .otp(otp)
                .expiredTime(LocalDateTime.now().plusSeconds(Util.getExpireOTPTime()))
                .ticketType(TicketType.REGISTRATION)
                .build();
        userService.save(user);
        userTicketService.save(ticket);
        emailService.sendOtpRegisterEmail(data.email(), otp, TicketType.REGISTRATION);
        return new MessageResponse(ticket.getId());
    }

    public AuthResponse authenticateGoogle(String email, String name) throws Exception {
        Optional<User> userOptional = userService.findByEmail(email);
        User user;
    
        if (userOptional.isEmpty()) {
            // Create a new user if not exists
            user = User.builder()
                    .email(email)
                    .name(name)
                    .role(Role.USER)
                    .enabled(true)
                    .build();
            userService.save(user);
        } else {
            user = userOptional.get();
        }
    
        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse changePassword(String email, String password) throws Exception {
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            throw new Exception("User not found");
        } else {
            User thisUser = user.get();
            thisUser.setPassword(passwordEncoder.encode(password));
            userService.save(thisUser);
            String jwtToken = jwtService.generateToken(thisUser);
            return new AuthResponse(jwtToken);
        }
    }

    public MessageResponse reGenerateOtp(String otp) {
        return new MessageResponse("RE-GENERATE OTP successfully!");
    }

    public AuthResponse verifyRegistration(UserTicket ticket, String otp) throws AuthException {
        userTicketService.verifyOtp(ticket, otp);
        User user = userService.findByEmail(ticket.getEmail()).orElseThrow(() -> new AuthException("User Not Found!"));
        user.setEnabled(true);
        userService.save(user);
        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse authenticate(AuthenticationRequest data) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            data.email(),
                            data.password()
                    )
            );
        } catch (AuthenticationException e) {
            throw new Exception("The account or password is incorrect!");
        }
        User user = userService.findByEmail(data.email()).orElseThrow(
                () -> new Exception("The account does not exist!")
        );
        if (user.isEnabled()) {
            String jwtToken = jwtService.generateToken(user);
            return new AuthResponse(jwtToken);
        } else {
            throw new Exception("The account or password is incorrect!");
        }
    }

    public MessageResponse sendCPOtp(String email) throws Exception {
        String otp = Util.generateOtp();
        var ticket = UserTicket.builder()
                .email(email)
                .otp(otp)
                .expiredTime(LocalDateTime.now().plusSeconds(Util.getExpireOTPTime()))
                .ticketType(TicketType.CHANGE_PASSWORD)
                .build();
        userTicketService.save(ticket);
        emailService.sendOtpRegisterEmail(email, otp, TicketType.CHANGE_PASSWORD);
        return new MessageResponse(ticket.getId());
    }
}
