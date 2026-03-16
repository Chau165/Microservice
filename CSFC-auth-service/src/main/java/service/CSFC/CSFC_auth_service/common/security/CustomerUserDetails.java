package service.CSFC.CSFC_auth_service.common.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import service.CSFC.CSFC_auth_service.model.entity.Users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomerUserDetails implements UserDetails {
    private final Users user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        var role = user.getRole();

        // role
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

        // permissions
        if (role.getPermissions() != null) {
            role.getPermissions().forEach(p ->
                    authorities.add(new SimpleGrantedAuthority(p.getName()))
            );
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getIsActive());
    }
}
