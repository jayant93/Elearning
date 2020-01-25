package com.ideyatech.opentides.um.security;

import com.ideyatech.opentides.core.security.JwtAuthenticationToken;
import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.SessionUser;
import com.ideyatech.opentides.core.entity.TokenHistory;
import com.ideyatech.opentides.um.entity.UserGroup;
import com.ideyatech.opentides.um.repository.ApplicationRepository;
import com.ideyatech.opentides.core.repository.TokenHistoryRepository;
import com.ideyatech.opentides.um.repository.UserGroupRepository;
import com.ideyatech.opentides.um.repository.cb.UserGroupCbRepository;
import com.ideyatech.opentides.um.security.event.UmAuthenticationFailureBadCredentialsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Username password authentication provider. Implementation is based on
 * {@link org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider}.
 * @author Gino
 */
public class UsernamePasswordAuthenticationProvider extends OpentidesAuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsernamePasswordAuthenticationProvider.class);

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenHistoryRepository tokenHistoryRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    private Boolean loadGroupsFromDb;

    private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
    private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken)authentication;

        LOGGER.debug("Authenticating user with username [{}]", authentication.getPrincipal());
        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED"
                : authentication.getName();
        UserDetails sessionUser = retrieveUser(username, authenticationToken);
        preAuthenticationChecks.check(sessionUser);

        return createSuccessAuthentication(username, authenticationToken, (SessionUser)sessionUser);
    }

    @Override
    @Transactional
    protected void afterSuccessAuthentication(String username, JwtAuthenticationToken token) {
        TokenHistory t = tokenHistoryRepository.findBySubject(username);
        if(t == null) {
            t = new TokenHistory();
            t.setSubject(username);
        }
        //tokenHistoryRepository.save(t);
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken)authentication;
        BaseUser user = userRepository.findByUsernameNotArchived(username);

        if(user == null) {
            throw new BadCredentialsException("User not found.");
        }

        String password = authenticationToken.getCredentials().toString();
        LOGGER.debug("Comparing entered password {} to credential password {}", password, user.getCredential().getPassword());
        if(!passwordEncoder.matches(password, user.getCredential().getPassword())) {
            AuthenticationException authenticationException = new BadCredentialsException("Invalid username/password.");
            LOGGER.debug("Publishing authenticationFailureBadCredetialEvent for appSecret [{}]",
                    authenticationToken.getApplicationSecret());
            eventPublisher.publishEvent(
                new UmAuthenticationFailureBadCredentialsEvent(authenticationToken,
                    authenticationToken.getApplicationSecret(), authenticationException));
            throw authenticationException;
        }

        Application application = applicationRepository.findByAppSecret(authenticationToken.getApplicationSecret());
        UserDetails sessionUser;
        if (loadGroupsFromDb != null && loadGroupsFromDb) {
            Set<UserGroup> ugs = user.getGroups();
            List<GrantedAuthority> gas = new ArrayList<>();
            for(UserGroup ug : ugs) {
                UserGroup fromDb = userGroupRepository.findByName(ug.getName());
                gas.addAll(fromDb.getGrantedAuthorities());
            }
            sessionUser = new SessionUser(user, gas);
        } else {
            sessionUser = new SessionUser(user);
        }

        if(application.getFailedLoginAttempts() != null && application.getFailedLoginAttempts() > 0) {
            if(isUserLockedOut(user, application.getFailedLoginAttempts(), application.getLockoutTime())) {
                sessionUser = new User(sessionUser.getUsername(),
                        sessionUser.getPassword(), sessionUser.isEnabled(),
                        sessionUser.isAccountNonExpired(),
                        sessionUser.isCredentialsNonExpired(), false,
                        sessionUser.getAuthorities());
                return sessionUser;
            }
        }

        return sessionUser;
    }

    private class DefaultPreAuthenticationChecks implements UserDetailsChecker {
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                LOGGER.debug("User account is locked");

                throw new LockedException("User account is locked");
            }

            if (!user.isEnabled()) {
                LOGGER.debug("User account is disabled");

                throw new DisabledException("User is disabled");
            }

            if (!user.isAccountNonExpired()) {
                LOGGER.debug("User account is expired");

                throw new AccountExpiredException("User account has expired");
            }
        }
    }

    private class DefaultPostAuthenticationChecks implements UserDetailsChecker {
        public void check(UserDetails user) {
            if (!user.isCredentialsNonExpired()) {
                LOGGER.debug("User account credentials have expired");

                throw new CredentialsExpiredException("User credentials have expired");
            }
        }
    }

    public void setPreAuthenticationChecks(UserDetailsChecker preAuthenticationChecks) {
        this.preAuthenticationChecks = preAuthenticationChecks;
    }

    public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
        this.postAuthenticationChecks = postAuthenticationChecks;
    }

    public void setLoadGroupsFromDb(Boolean loadGroupsFromDb) {
        this.loadGroupsFromDb = loadGroupsFromDb;
    }
}
