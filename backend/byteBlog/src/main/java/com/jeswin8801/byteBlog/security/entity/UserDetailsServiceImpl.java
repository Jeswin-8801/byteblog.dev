package com.jeswin8801.byteBlog.security.entity;

import com.jeswin8801.byteBlog.repository.UserRepository;
import com.jeswin8801.byteBlog.util.exceptions.enums.AuthExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security will load User details to perform authentication & authorization.
 * <p>
 * <b><i>UserDetailsService</i></b> interface is implemented to retrieve the <b><i>UserDetails</i></b> object that stores the necessary user data.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return UserDetailsImpl.build(
                userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException(AuthExceptions.INCORRECT_LOGIN_CREDENTIALS.getMessage()))
        );
    }
}
