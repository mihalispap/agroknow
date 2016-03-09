package com.agroknow.search.web.security;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

/**
 *
 * @author aggelos
 */
@Component("tokenBasedPreAuthenticatedProcessingFilter")
public class TokenBasedPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenBasedAuthenticationDetailsSource authenticationDetailsSource;

    @Override
    public void afterPropertiesSet() {
        super.setAuthenticationManager(authenticationManager);
        super.setAuthenticationDetailsSource(authenticationDetailsSource);
        super.afterPropertiesSet();
    }

    /**
     * Return the user's email based on the provided token.
     *
     * @param httpRequest
     * @return
     */
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpRequest) {
        Object principal = null;
        TokenBasedAuthenticationDetails details = null;
        try {
            details = (TokenBasedAuthenticationDetails) getAuthenticationDetailsSource().buildDetails(httpRequest);
            principal = details.getUserEmail();

            if (logger.isDebugEnabled()) {
                logger.debug("PreAuthenticated Token found for Principal: " + principal);
            }
        } catch(Exception ex) {
            logger.warn("PreAuthenticationFilter could not locate user's principal from token: " + (details==null ? null : details.getToken()), ex);
        }

        //principal="api@agroknow.com";
        return principal;
    }

    /**
     * For J2EE container-based authentication there is no generic way to
     * retrieve the credentials, as such this method returns a fixed dummy
     * value.
     *
     * @param httpRequest
     * @return
     */
    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpRequest) {
        return "N/A";
    }
}
