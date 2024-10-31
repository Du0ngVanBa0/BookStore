package duongvanbao.Book.Store.service;

import duongvanbao.Book.Store.config.JwtService;
import duongvanbao.Book.Store.dto.auth.*;
import duongvanbao.Book.Store.dto.user.RoleName;
import duongvanbao.Book.Store.model.Role;
import duongvanbao.Book.Store.model.User;
import duongvanbao.Book.Store.model.UserRole;
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
    private UserRoleService userRoleService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserTicketService userTicketService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, UserRoleService userRoleService, RoleService roleService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRoleService = userRoleService;
        this.roleService = roleService;
    }

    public String register(RegisterRequest data) throws Exception {
        String otp = Util.generateOtp();
        var user = User.builder()
                .name(data.name())
                .email(data.email())
                .password(passwordEncoder.encode(data.password()))
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
        return ticket.getId();
    }

    public TokenResponse authenticateGoogle(GoogleUser thisUser) throws Exception {
        Optional<User> userOptional = userService.findByEmail(thisUser.email());
        User user;
    
        if (userOptional.isEmpty()) {
            // Create a new user if not exists
            user = User.builder()
                    .email(thisUser.email())
                    .name(thisUser.name())
                    .picture(thisUser.picture())
                    .enabled(true)
                    .build();
            userService.save(user);
            checkAndSolveIfNotExistRoleUser(user);
        } else {
            user = userOptional.get();
        }
    
        String jwtToken = jwtService.generateToken(user);
        return new TokenResponse(jwtToken);
    }

    private void checkAndSolveIfNotExistRoleUser(User user) {
        if (roleService.findByName(RoleName.USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(RoleName.USER);
            roleService.save(userRole);
        }
        Role role = roleService.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        UserRole userRoleEntry = new UserRole(user, role);
        userRoleService.save(userRoleEntry);
    }

    public TokenResponse changePassword(String email, String password) throws Exception {
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            throw new Exception("User not found");
        } else {
            User thisUser = user.get();
            thisUser.setPassword(passwordEncoder.encode(password));
            userService.save(thisUser);
            String jwtToken = jwtService.generateToken(thisUser);
            return new TokenResponse(jwtToken);
        }
    }

    public MessageResponse reGenerateOtp(String otp) {
        return new MessageResponse("RE-GENERATE OTP successfully!");
    }

    public TokenResponse verifyRegistration(UserTicket ticket, String otp) throws AuthException {
        userTicketService.verifyOtp(ticket, otp);
        User user = userService.findByEmail(ticket.getEmail()).orElseThrow(() -> new AuthException("User Not Found!"));
        user.setEnabled(true);
        userService.save(user);
        String jwtToken = jwtService.generateToken(user);
        return new TokenResponse(jwtToken);
    }

    public TokenResponse authenticate(AuthenticationRequest data) throws Exception {
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
            return new TokenResponse(jwtToken);
        } else {
            throw new Exception("The account or password is incorrect!");
        }
    }

    public TicketResponse sendCPOtp(String email) throws Exception {
        String otp = Util.generateOtp();
        var ticket = UserTicket.builder()
                .email(email)
                .otp(otp)
                .expiredTime(LocalDateTime.now().plusSeconds(Util.getExpireOTPTime()))
                .ticketType(TicketType.CHANGE_PASSWORD)
                .build();
        userTicketService.save(ticket);
        emailService.sendOtpRegisterEmail(email, otp, TicketType.CHANGE_PASSWORD);
        return new TicketResponse(ticket.getId());
    }
}
