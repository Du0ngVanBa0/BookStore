package duongvanbao.Book.Store.controller;

import duongvanbao.Book.Store.dto.*;
import duongvanbao.Book.Store.model.User;
import duongvanbao.Book.Store.model.UserTicket;
import duongvanbao.Book.Store.service.AuthService;
import duongvanbao.Book.Store.service.EmailService;
import duongvanbao.Book.Store.service.UserService;
import duongvanbao.Book.Store.service.UserTicketService;
import duongvanbao.Book.Store.utils.Util;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTicketService userTicketService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest data) {
        Optional<User> userOptional = userService.findByEmail(data.email());
        if (userOptional.isPresent()){
            User currentUser = userOptional.get();
            if (currentUser.isEnabled()){
                return new ResponseEntity<>(new MessageResponse("Email has been registered!"), HttpStatus.BAD_REQUEST);
            } else {
                userService.deleteById(currentUser.getId());
            }
        }
        MessageResponse res;
        try {
            res = authService.register(data);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("OTP registered failed!"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/send-cp-otp")
    public ResponseEntity<?> sendFpOtp(@RequestParam String email) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent() && userOptional.get().isEnabled()){
            try {
                MessageResponse res =  authService.sendCPOtp(email);
                return new ResponseEntity<>(res, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>("OTP send failed!", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("Can't find this account!"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verify-cp-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyRequest data) {
        Optional<UserTicket> ticketOptional = userTicketService.findById(data.ticketId());
        if (ticketOptional.isPresent()) {
            // check is ticket valid (and not expired)
            if (ticketOptional.get().getExpiredTime().isAfter(LocalDateTime.now())) {
                try {
                    userTicketService.verifyOtp(ticketOptional.get(), data.otp());
                    userTicketService.activeTicket(ticketOptional.get());
                    return new ResponseEntity<>(new MessageResponse("OTP verified successfully!"), HttpStatus.OK);
                } catch (AuthException e) {
                    return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
                }
            } else {
                userTicketService.deleteByTicketID(ticketOptional.get().getId());
                return new ResponseEntity<>(new MessageResponse("Expired Time!"), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new MessageResponse("This ticket does not exist!"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/regenerate-otp")
    public ResponseEntity<?> reGenerateOtp(@RequestParam String ticketId) {
        Optional<UserTicket> ticketOptional = userTicketService.findById(ticketId);
        if (ticketOptional.isPresent()) {
            UserTicket thisTicket = ticketOptional.get();
            if (!thisTicket.isActive() && !thisTicket.isVerified() && thisTicket.getExpiredTime().isAfter(LocalDateTime.now())) {
                String newOtp = userTicketService.reGenerateOtp(thisTicket);
                try {
                    emailService.sendOtpRegisterEmail(thisTicket.getEmail(), newOtp, thisTicket.getTicketType());
                } catch (Exception e) {
                    return new ResponseEntity<>(new MessageResponse("Cant send otp now!"), HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(new MessageResponse("Regenerate OTP successfully!"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new MessageResponse("Time Expired. Try another!"), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("Ticket does not exist!"), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest data) {
        Optional<UserTicket> ticketOptional = userTicketService.findById(data.ticketId());
        if (ticketOptional.isPresent()) {
            // check is ticket valid (and not expired)
            UserTicket thisTicket = ticketOptional.get();
            if (!thisTicket.isActive() && thisTicket.isVerified() && thisTicket.getVerifiedTime().plusSeconds(Util.getExpireTimeChangePass()).isAfter(LocalDateTime.now())) {
                AuthResponse res;
                try {
                    res = authService.changePassword(thisTicket.getEmail(), data.password());
                    userTicketService.activeTicket(ticketOptional.get());
                    return new ResponseEntity<>(res, HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(new MessageResponse("Time Expired. Try another!"), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new MessageResponse("This ticket does not exist!"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/verify-registration")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyRequest data) {
        Optional<UserTicket> ticketOptional = userTicketService.findById(data.ticketId());
        if (ticketOptional.isPresent()) {
            // check is ticket valid (and not expired)
            if (ticketOptional.get().getExpiredTime().isAfter(LocalDateTime.now())) {
                AuthResponse res;
                try {
                    res = authService.verifyRegistration(ticketOptional.get(), data.otp());
                } catch (AuthException e) {
                    return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(res, HttpStatus.CREATED);
            } else {
                userTicketService.deleteByTicketID(ticketOptional.get().getId());
                return new ResponseEntity<>(new MessageResponse("Expired Time!"), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new MessageResponse("This ticket does not exist!"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest data) {
        try {
            return new ResponseEntity<>(authService.authenticate(data), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
