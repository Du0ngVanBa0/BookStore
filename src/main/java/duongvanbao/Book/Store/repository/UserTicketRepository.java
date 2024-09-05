package duongvanbao.Book.Store.repository;

import duongvanbao.Book.Store.model.UserTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTicketRepository extends JpaRepository<UserTicket, String> {
}
