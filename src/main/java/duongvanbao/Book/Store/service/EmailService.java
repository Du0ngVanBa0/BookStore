package duongvanbao.Book.Store.service;

import duongvanbao.Book.Store.dto.auth.TicketType;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.username.name}")
    private String fromName;

    public void sendOtpRegisterEmail(String to, String otp, TicketType ticketType) throws Exception {
        String action = "";
        if (ticketType == TicketType.REGISTRATION) {
            action = "registering a new account";
        } else if (ticketType == TicketType.LOGIN) {
            action = "logging in";
        } else if (ticketType == TicketType.CHANGE_PASSWORD) {
            action = "resetting the password";
        }
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(fromEmail, fromName);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(otp + " is your otp to verify your EmiliaTan's account");
        mimeMessageHelper.setText(
                "<html style='color: black;'>" +
                        "<body style='font-size: 20px;'>" +
                        "<div style='text-align: start;'>" +
                        "<img src='https://th.bing.com/th?id=OIP.LvjvRJyXXoYIzQYn33igngHaEK&w=312&h=200&c=12&rs=1&qlt=99&o=6&dpr=1.8&pid=13.1' alt='Logo' style='width: 300px; height: auto; margin-bottom: 20px;' />" +
                        "</div>" +
                        "<p>Hello,</p>" +
                        "<p>You are " + action + ". Your verification code is <strong style='color: #0033cc;'>" + otp + "</strong>.</p>" +
                        "<p>Please complete the verification within 10 minutes.</p>" +
                        "<p>Best regards,<br/>EmiliaTan</p>" +
                        "</body>" +
                        "</html>", true);
        mailSender.send(mimeMessage);
    }
}
