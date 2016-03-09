package com.agroknow.search.web.security;

/**
 *
 * @author aggelos
 */
public class TokenBasedAuthenticationDetails {

    private final String remoteAddress;
    private final String sessionId;
    private final String token;
    private final String userEmail;

    public TokenBasedAuthenticationDetails(String remoteAddress, String sessionId, String userEmail, String token) {
        this.remoteAddress = remoteAddress;
        this.sessionId = sessionId;
        this.userEmail = userEmail;
        this.token = token;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TokenBasedAuthenticationDetails other = (TokenBasedAuthenticationDetails) obj;
        if ((this.remoteAddress == null) ? (other.remoteAddress != null) : !this.remoteAddress.equals(other.remoteAddress)) {
            return false;
        }
        if ((this.sessionId == null) ? (other.sessionId != null) : !this.sessionId.equals(other.sessionId)) {
            return false;
        }
        if ((this.userEmail == null) ? (other.userEmail != null) : !this.userEmail.equals(other.userEmail)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.remoteAddress != null ? this.remoteAddress.hashCode() : 0);
        hash = 17 * hash + (this.sessionId != null ? this.sessionId.hashCode() : 0);
        hash = 17 * hash + (this.userEmail != null ? this.userEmail.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "TokenBasedAuthenticationDetails{" + "remoteAddress=" + remoteAddress + ", sessionId=" + sessionId + ", userEmail=" + userEmail + '}';
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getToken() {
        return token;
    }
}
