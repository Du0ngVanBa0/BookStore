package duongvanbao.Book.Store.model;

import duongvanbao.Book.Store.dto.user.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private RoleName name;

    public Role() {
    }

    public Role(String id, RoleName name) {
        this.id = id;
        this.name = name;
    }
}

