    package duongvanbao.Book.Store.model;

    import jakarta.persistence.*;
    import lombok.*;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.time.LocalDateTime;
    import java.util.Collection;
    import java.util.List;
    import java.util.stream.Collectors;

    @Builder
    @Entity
    @NoArgsConstructor
    @AllArgsConstructor
    public class User implements UserDetails {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Getter
        private String id;

        private String email;

        @Setter
        private String password;

        @Getter
        @Setter
        private String picture;

        @Getter
        private String name;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        private List<UserRole> userRoles;

        @Setter
        private boolean enabled;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return userRoles.stream()
                    .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName().name()))
                    .collect(Collectors.toList());
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
