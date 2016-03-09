package com.agroknow.search.web.security;

import com.agroknow.search.domain.entities.User;
import com.agroknow.search.domain.services.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

/**
 *
 * @author aggelos
 */
@Component
public class TokenBasedAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, TokenBasedAuthenticationDetails> {

    @Autowired
    private UserService userService;

    @Override
    public TokenBasedAuthenticationDetails buildDetails(HttpServletRequest context) {
        // get remote address
        String remoteAddress = context.getRemoteAddr();

        // get session id if exists
        HttpSession session = context.getSession(false);
        String sessionId = (session != null) ? session.getId() : null;

        // find token in request in either api_key request param or Authorization http header
        String token = context.getParameter("api_key");
        if(StringUtils.isEmpty(token)) { // search in header
            token = context.getHeader("Authorization");
            if(!StringUtils.isEmpty(token) && token.trim().startsWith("AGRO ")) {
                token = token.trim().substring(5);
            }
        }

        // get user's email based on the token provided
        String userEmail = null;
        try {
            User user = userService.findByToken(token);
            userEmail = user.getEmail();
        } catch(Exception ex) {/*leave userEmail null on exception*/}

        //userEmail="info@agroknow.com";
        //token="mytok";
        return new TokenBasedAuthenticationDetails(remoteAddress, sessionId, userEmail, token);
    }

}
