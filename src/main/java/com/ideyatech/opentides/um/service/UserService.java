package com.ideyatech.opentides.um.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.Division;
import com.ideyatech.opentides.um.security.event.UmAuthenticationSuccessEvent;

/**
 * @author Gino.
 */
public interface UserService {

    /**
     * Updates last login of the user from a login event
     */
    void updateLogin(
            UmAuthenticationSuccessEvent umAuthenticationSuccessEvent);

    /**
     * Unlock a locked-out user
     *
     * @param username
     */
    public void unlockUser(String username);

    /**
     * Logs the event of logout
     */
    void updateLogout(Authentication auth);

    /**
     * Update the details of the user when login failed.
     * <p>
     * This should increment the failed login count and update the last failed
     * login timestamp
     * </p>
     *
     * @param username
     *            username of the user
     * @param timestamp
     *            the last failed login timestamp
     */
    void updateFailedLogin(String username, long timestamp);

    /**
     * Check if the user with the specified username is locked out.
     *
     * @param username
     *            username of the user
     * @param maxAttempts
     *            maximum number of attempts
     * @param lockOutTime
     *            lockout time in seconds
     *
     * @return
     */
    boolean isUserLockedOut(String username, long maxAttempts,
                                   long lockOutTime);

    /**
     *
     * @param baseUser
     * @return
     */
    boolean isPasswordCanBeReused(BaseUser baseUser, String password, int maxTimes);
    
    /**
     * Retrieves the access credential of the given username.
     * @return
     */
    public Map<String,Object> getUserAccess(String username);

    /**
     * Retrieves the access credential of the given username.
     * @return
     */
    public Map<String,Object> getUserAccessNoChildDivision(String username);
}
