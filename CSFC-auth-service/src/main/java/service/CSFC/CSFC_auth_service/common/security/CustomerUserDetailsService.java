package service.CSFC.CSFC_auth_service.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.model.entity.Users;
import service.CSFC.CSFC_auth_service.repository.UsersRepository;


@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {
    private final UsersRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + email));

        return new CustomerUserDetails(user);
    }
}
