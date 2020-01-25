package com.ideyatech.opentides.um.security;

import com.ideyatech.opentides.core.security.JwtAuthenticationToken;
import com.ideyatech.opentides.core.util.JwtUtil;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.SessionUser;
import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.security.event.UmAuthenticationSuccessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * Authentication provider for opentides.
 *
 * @author Gino
 */
public abstract class OpentidesAuthenticationProvider implements AuthenticationProvider {

    @Value("${token.age}")
    private Integer tokenAge;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    protected GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    protected Authentication createSuccessAuthentication(Object principal,
                                                         JwtAuthenticationToken authentication, SessionUser user) {

        String token = JwtUtil.generateToken(authentication.getApplicationSecret(), user, tokenAge, null);

        JwtAuthenticationToken jwtAuthenticationToken =
                new JwtAuthenticationToken(token, authentication.getApplicationSecret());
        jwtAuthenticationToken.setId(user.getNosqlId() != null ? user.getNosqlId() : "" + user.getId());
        jwtAuthenticationToken.setFirstName((String)user.getProfile().get("firstName"));
        jwtAuthenticationToken.setLastName((String)user.getProfile().get("lastName"));
        jwtAuthenticationToken.setGroups((List<String>)user.getAdditionalProperties().get("groups"));
        jwtAuthenticationToken.setDivisions((List<String>)user.getAdditionalProperties().get("divisions"));
        jwtAuthenticationToken.setHomeDepartment((String)user.getAdditionalProperties().get("homeDepartmentKey"));
        jwtAuthenticationToken.setHomeSection((String)user.getAdditionalProperties().get("homeSectionKey"));

        //Publish authentication success event...
        WebAuthenticationDetails authenticationDetails =
                new WebAuthenticationDetails(((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes()).getRequest());
        authentication.setDetails(authenticationDetails);

        afterSuccessAuthentication((String)principal, jwtAuthenticationToken);

        eventPublisher.publishEvent(new UmAuthenticationSuccessEvent(authentication, authentication.getApplicationSecret()));

        return jwtAuthenticationToken;
    }

    protected boolean isUserLockedOut(BaseUser user, long maxAttempts, long lockOutTime) {
        if(user != null) {
            if(user.getFailedLoginCount() != null && user.getFailedLoginCount() >= maxAttempts) {
                long elapsedTime = System.currentTimeMillis() -
                        (user.getLastFailedLoginMillis() == null ? 0 : user.getLastFailedLoginMillis());
                if(elapsedTime < 1000 * lockOutTime) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void afterSuccessAuthentication(String username, JwtAuthenticationToken token) {

    }

    /**
     *
     * @param username
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    protected abstract UserDetails retrieveUser(String username,
                                                UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException;
}
