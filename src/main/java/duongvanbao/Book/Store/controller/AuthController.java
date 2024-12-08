package duongvanbao.Book.Store.controller;

import duongvanbao.Book.Store.dto.auth.*;
import duongvanbao.Book.Store.dto.utils.errorResponse.ErrorResponse;
import duongvanbao.Book.Store.dto.utils.successResponse.SuccessResponseWithData;
import duongvanbao.Book.Store.dto.utils.successResponse.SuccessResponseWithoutData;
import duongvanbao.Book.Store.model.User;
import duongvanbao.Book.Store.model.UserTicket;
import duongvanbao.Book.Store.service.*;
import duongvanbao.Book.Store.utils.Util;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;
    private UserService userService;
    private UserTicketService userTicketService;
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest data) {
        Optional<User> userOptional = userService.findByEmail(data.email());
        if (userOptional.isPresent()){
            User currentUser = userOptional.get();
            if (currentUser.isEnabled()){
                return new ResponseEntity<>(new SuccessResponseWithoutData("Email has been registered!"), HttpStatus.OK);
            } else {
                userService.deleteById(currentUser.getId());
            }
        }
        String res;
        try {
            res = authService.register(data);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("OTP registered failed! " + e), HttpStatus.OK);
        }
        return new ResponseEntity<>(new SuccessResponseWithData<>(res), HttpStatus.CREATED);
    }

    @GetMapping("/send-cp-otp")
    public ResponseEntity<?> sendFpOtp(@RequestParam String email) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent() && userOptional.get().isEnabled()){
            try {
                TicketResponse res =  authService.sendCPOtp(email);
                return new ResponseEntity<>(new SuccessResponseWithData<>(res), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(new ErrorResponse("OTP send failed!"), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new ErrorResponse("Can't find this account!"), HttpStatus.OK);
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
                    return new ResponseEntity<>(new SuccessResponseWithoutData("OTP verified successfully!"), HttpStatus.OK);
                } catch (AuthException e) {
                    return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.OK);
                }
            } else {
                userTicketService.deleteByTicketID(ticketOptional.get().getId());
                return new ResponseEntity<>(new ErrorResponse("Expired Time!"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new ErrorResponse("This ticket does not exist!"), HttpStatus.OK);
    }

    @GetMapping("/regenerate-otp")
    public ResponseEntity<?> reGenerateOtp(@RequestParam String ticketId) {
        Optional<UserTicket> ticketOptional = userTicketService.findById(ticketId);
        if (ticketOptional.isPresent()) {
            UserTicket thisTicket = ticketOptional.get();
            if (!thisTicket.isActive() && !thisTicket.isVerified() && thisTicket.getExpiredTime().isAfter(LocalDateTime.now())) {
                try {
                    String newOtp = userTicketService.reGenerateOtp(thisTicket);
                    emailService.sendOtpRegisterEmail(thisTicket.getEmail(), newOtp, thisTicket.getTicketType());
                } catch (Exception e) {
                    return new ResponseEntity<>(new ErrorResponse("Cant send otp now!"), HttpStatus.OK);
                }
                return new ResponseEntity<>(new SuccessResponseWithoutData("Regenerate OTP successfully!"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ErrorResponse("Time Expired. Try another!"), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new ErrorResponse("Ticket does not exist!"), HttpStatus.OK);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest data) {
        Optional<UserTicket> ticketOptional = userTicketService.findById(data.ticketId());
        if (ticketOptional.isPresent()) {
            // check is ticket valid (and not expired)
            UserTicket thisTicket = ticketOptional.get();
            if (thisTicket.isActive() && thisTicket.isVerified() && thisTicket.getVerifiedTime().plusSeconds(Util.getExpireTimeChangePass()).isAfter(LocalDateTime.now())) {
                TokenResponse res;
                try {
                    res = authService.changePassword(thisTicket.getEmail(), data.password());
                    userTicketService.activeTicket(ticketOptional.get());
                    return new ResponseEntity<>(new SuccessResponseWithData<>(res), HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new ErrorResponse("Time Expired. Try another!"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new ErrorResponse("This ticket does not exist!"), HttpStatus.OK);
    }

    @PostMapping("/verify-registration")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyRequest data) {
        Optional<UserTicket> ticketOptional = userTicketService.findById(data.ticketId());
        if (ticketOptional.isPresent()) {
            // check is ticket valid (and not expired)
            if (ticketOptional.get().getExpiredTime().isAfter(LocalDateTime.now())) {
                TokenResponse res;
                try {
                    res = authService.verifyRegistration(ticketOptional.get(), data.otp());
                } catch (AuthException e) {
                    return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.OK);
                }
                return new ResponseEntity<>(new SuccessResponseWithData<>(res), HttpStatus.OK);
            } else {
                userTicketService.deleteByTicketID(ticketOptional.get().getId());
                return new ResponseEntity<>(new ErrorResponse("Expired Time!"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new ErrorResponse("This ticket does not exist!"), HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest data) {
        try {
            return new ResponseEntity<>(new SuccessResponseWithData<>(authService.authenticate(data)), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.OK);
        }
    }
}
