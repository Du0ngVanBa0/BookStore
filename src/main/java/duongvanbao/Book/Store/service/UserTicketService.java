package duongvanbao.Book.Store.service;

import duongvanbao.Book.Store.model.UserTicket;
import duongvanbao.Book.Store.repository.UserTicketRepository;
import duongvanbao.Book.Store.utils.Util;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserTicketService {
    @Autowired
    private UserTicketRepository userTicketRepository;

    public Optional<UserTicket> findById(String ticketID) {
        return userTicketRepository.findById(ticketID);
    }

    public void deleteByTicketID(String ticketID) {
        userTicketRepository.deleteById(ticketID);
    }

    public void verifyOtp(UserTicket ticket, String otp) throws AuthException {
        //Check is the otp expired or invalid
        if (!ticket.getOtp().equals(otp)) {
            throw new AuthException("Incorrect OTP!");
        }
        verifyTicket(ticket.getId());
    }
    public void verifyTicket(String ticketID) {
        Optional<UserTicket> userTicket = userTicketRepository.findById(ticketID);
        if (userTicket.isPresent()) {
            UserTicket ticket = userTicket.get();
            ticket.setVerified(true);
            ticket.setVerifiedTime(LocalDateTime.now());
            userTicketRepository.save(ticket);
        }
    }
    public void activeTicket(UserTicket ticket) {
        ticket.setActive(true);
        userTicketRepository.save(ticket);
    }
    public void save(UserTicket ticket) {
        userTicketRepository.save(ticket);
    }

    public String reGenerateOtp(UserTicket thisTicket) {
        String newOtp = Util.generateOtp();
        while (newOtp.equals(thisTicket.getOtp())) {
            newOtp = Util.generateOtp();
        }
        thisTicket.setOtp(newOtp);
        thisTicket.setExpiredTime(LocalDateTime.now().plusSeconds(Util.getExpireOTPTime()));
        userTicketRepository.save(thisTicket);
        return newOtp;
    }
}
