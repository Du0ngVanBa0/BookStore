    package duongvanbao.Book.Store.model;

    import jakarta.persistence.*;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.Setter;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.time.LocalDateTime;
    import java.util.Collection;
    import java.util.List;

    @Builder
    @Entity
    public class User implements UserDetails {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Getter
        private String id;

        private String email;

        @Setter
        private String password;

        @Getter
        private String name;

        @Enumerated(EnumType.STRING)
        private Role role;

        @Setter
        private boolean enabled;

        public User(String id, String email, String password, String name, Role role, boolean enabled) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.name = name;
            this.role = role;
            this.enabled = enabled;
        }

        public User() {
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority(role.name()));
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() {
            return UserDetails.super.isAccountNonExpired();
        }

        @Override
        public boolean isAccountNonLocked() {
            return UserDetails.super.isAccountNonLocked();
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return UserDetails.super.isCredentialsNonExpired();
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }
    }
