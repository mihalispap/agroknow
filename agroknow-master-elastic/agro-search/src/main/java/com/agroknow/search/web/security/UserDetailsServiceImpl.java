package com.agroknow.search.web.security;

import com.agroknow.search.domain.entities.User;
import com.agroknow.search.domain.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author aggelos
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Autowired
    private UserService userService;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken t) throws UsernameNotFoundException {
        String principal = (String)t.getPrincipal(); // as principal we keep user's email
        try {
            User user = userService.findByUserEmail(principal);
            if(user == null) {
                throw new RuntimeException();
            }
            
            return user;
        } catch(RuntimeException ex) {
            throw new UsernameNotFoundException("Could not found user based on principal: " + principal, ex);
        }

    }

}
