package duongvanbao.Book.Store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyRequest(
        @NotBlank(message = "Ticket Id is required!")
        String ticketId,
        @NotBlank(message = "OTP is required!")
        String otp) {
}
