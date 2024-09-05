package duongvanbao.Book.Store.model;

import duongvanbao.Book.Store.dto.TicketType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Entity
public class UserTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private String id;

    @Getter
    @Setter
    private String email;

    @Setter
    @Getter
    private String otp;

    @Getter
    @Setter
    private LocalDateTime expiredTime;

    @Setter
    @Getter
    private boolean isVerified = false;

    @Getter
    @Setter
    private LocalDateTime verifiedTime;

    @Setter
    @Getter
    private boolean isActive = false;

    @Enumerated(EnumType.STRING)
    @Getter
    private TicketType ticketType;

    public UserTicket(String id, String email, String otp, LocalDateTime expiredTime, boolean isVerified, LocalDateTime verifiedTime, boolean isActive, TicketType ticketType) {
        this.id = id;
        this.email = email;
        this.otp = otp;
        this.expiredTime = expiredTime;
        this.isVerified = isVerified;
        this.verifiedTime = verifiedTime;
        this.isActive = isActive;
        this.ticketType = ticketType;
    }

    public UserTicket() {
    }
}
