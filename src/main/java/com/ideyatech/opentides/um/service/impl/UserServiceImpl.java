package com.ideyatech.opentides.um.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ideyatech.opentides.um.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ideyatech.opentides.core.repository.AuditLogRepository;
import com.ideyatech.opentides.um.repository.ApplicationRepository;
import com.ideyatech.opentides.um.repository.PasswordHistoryRepository;
import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.security.event.AbstractUMAuthenticationEvent;
import com.ideyatech.opentides.um.security.event.AbstractUMAuthenticationFailureEvent;
import com.ideyatech.opentides.um.security.event.UmAuthenticationFailureBadCredentialsEvent;
import com.ideyatech.opentides.um.security.event.UmAuthenticationSuccessEvent;
import com.ideyatech.opentides.um.service.DivisionService;
import com.ideyatech.opentides.um.service.UserService;
import com.ideyatech.opentides.um.util.SecurityUtil;

/**
 * @author Gino
 */
@Service
public class UserServiceImpl implements UserService, ApplicationListener<AbstractUMAuthenticationEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static Map<String, Map<String, Object>> userAccessCache = new HashMap<String, Map<String, Object>>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;
    
    @Autowired
    private DivisionService divisionService;

    @Override
    public void updateLogin(UmAuthenticationSuccessEvent umAuthenticationSuccessEvent) {
        String username = umAuthenticationSuccessEvent.getAuthentication().getName();
        if(username != null && !username.isEmpty()) {
            BaseUser user = userRepository.findByUsername(username);
            WebAuthenticationDetails details = (WebAuthenticationDetails) umAuthenticationSuccessEvent.getAuthentication().getDetails();
            // clear user access cache upon login
            userAccessCache.remove(user.getNosqlId());
            if(details != null) {
                String address = details.getRemoteAddress();
                if (user.getTotalLoginCount()== null) {
                    user.setTotalLoginCount(1l);
                } else {
                    user.setTotalLoginCount(user.getTotalLoginCount() + 1);
                }
                user.setPrevLoginIP(user.getLastLoginIP());
                user.setLastLoginIP(address);
                user.setLastLogin(new Date());
                user.setSkipAudit(true);
                userRepository.save(user);

                // force the audit user details
                String completeName = user.getCompleteName() + " [" + username + "] ";
                user.setAuditUserId(user.getId());
                user.setAuditUsername(username);
                user.setSkipAudit(false);
                String message = completeName + " has logged-in. IP Address: " + user.getLastLoginIP();
                auditLogRepository.logEvent(message, user);
            }
        }
    }

    @Override
    public void updateLogout(Authentication auth) {
        if (auth==null) return;
        Object userObj = auth.getPrincipal();
        if (userObj instanceof SessionUser) {
            SessionUser sessionUser = (SessionUser) userObj;
            String username = sessionUser.getUsername();
            // also add log to audit history log
            BaseUser user = userRepository.findByUsername(username);
            String completeName = user.getCompleteName() + " ["+ username + "] ";
            // force the audit user details
            user.setAuditUserId(user.getId());
            user.setAuditUsername(username);
            String message = completeName + " has logged-out. IP Address: " + user.getLastLoginIP();
            auditLogRepository.logEvent(message, user);
        }
    }

    @Override
    public void updateFailedLogin(String username, long timestamp) {
        BaseUser user = userRepository.findByUsername(username);
        if(user != null) {
            user.incrementFailedLoginCount();
            user.setLastFailedLoginMillis(timestamp);
            userRepository.save(user);
        }
    }

    @Override
    public void unlockUser(String username) {
        BaseUser user = userRepository.findByUsername(username);
        if (user!=null) {
            user.resetFailedLoginCount();
            userRepository.save(user);
        }
    }

    @Override
    public boolean isUserLockedOut(String username, long maxAttempts, long lockOutTime) {
        BaseUser user = userRepository.findByUsername(username);
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

    @Override
    public boolean isPasswordCanBeReused(BaseUser user, String password, int maxTimes) {
        List<PasswordHistory> phs = passwordHistoryRepository.findByUserOrderByIdDesc(user);
        for(int i = 0; i < phs.size(); i++) {
            PasswordHistory ph = phs.get(i);
            if (ph.getPassword().equals(password)) {
                if (i < maxTimes) {
                    return false;
                }
            }
        }
        return true;
    }
    

	@Override
    public Map<String,Object> getUserAccess(String username) {
    		Map<String, Object> result = new HashMap<>();    	
    		BaseUser user = userRepository.findByUsername(username);
    		if (userAccessCache.containsKey(user.getNosqlId())) {
    			// use cache if found
    			result = userAccessCache.get(user.getNosqlId());
    		} else {
    			result.put("NOSQL_ID", user.getNosqlId());
    	
    	    		if (SecurityUtil.currentUserHasPermission("ACCESS_ALL"))
    	    			result.put("ACCESS_FILTER", "ACCESS_ALL");
    	    		else if (SecurityUtil.currentUserHasPermission("ACCESS_DIVISION"))
    				result.put("ACCESS_FILTER", "ACCESS_DIVISION");
    	    		else if (SecurityUtil.currentUserHasPermission("ACCESS_OWN"))
    				result.put("ACCESS_FILTER", "ACCESS_OWN");
    	    		else
    	    			result.put("ACCESS_FILTER", "ACCESS_NONE");
    	    		
    	    		Set<Division> superDescendants = new HashSet<Division>();
                    Set<UserGroup> userGroups = new HashSet<UserGroup>();
    	    		
    	    		if (user!=null) {
    	    			// get all divisions of the user
    	        		superDescendants.addAll(divisionService.findDescendants(user.getHomeDepartment()));
    	        		for(Division d: user.getDivisions()) {
    	        			superDescendants.addAll(divisionService.findDescendants(d.getKey()));
    	        		}
    	        		for (UserGroup userGroup : user.getGroups()) {
    	        		    userGroups.add(userGroup);
                        }
    	    		}
    	    		result.put("ORGUNITS", superDescendants);
    	    		
    	    		Set<String> uDivKeys = new HashSet<String>();
    	    		for(Division div:superDescendants) {
    	    			uDivKeys.add(div.getKey());    			
    	    		}
    	    		result.put("ORGUNIT_KEYS", uDivKeys);

                    Set<String> userGroupKeys = new HashSet<String>();
                    for(UserGroup userGroup : userGroups) {
                        userGroupKeys.add(userGroup.getKey());
                    }
                    result.put("USERGROUP_KEYS", userGroupKeys);
    	    		
    	    		// add to cache
    	    		userAccessCache.put(user.getNosqlId(), result);
    		}
    		return result;
    }

	@Override
    public Map<String,Object> getUserAccessNoChildDivision(String username) {
    		Map<String, Object> result = new HashMap<>();
    		BaseUser user = userRepository.findByUsername(username);
    		if (userAccessCache.containsKey(user.getNosqlId())) {
    			// use cache if found
    			result = userAccessCache.get(user.getNosqlId());
    		} else {
    			result.put("NOSQL_ID", user.getNosqlId());

    	    		if (SecurityUtil.currentUserHasPermission("ACCESS_ALL"))
    	    			result.put("ACCESS_FILTER", "ACCESS_ALL");
    	    		else if (SecurityUtil.currentUserHasPermission("ACCESS_DIVISION"))
    				result.put("ACCESS_FILTER", "ACCESS_DIVISION");
    	    		else if (SecurityUtil.currentUserHasPermission("ACCESS_OWN"))
    				result.put("ACCESS_FILTER", "ACCESS_OWN");
    	    		else
    	    			result.put("ACCESS_FILTER", "ACCESS_NONE");

    	    		Set<Division> divisions = new HashSet<Division>();
                    Set<UserGroup> userGroups = new HashSet<UserGroup>();

    	    		if (user!=null) {
    	    			// get all divisions of the user
                        if (user.getHomeDepartment() != null) {
                            divisions.add(user.getHomeDepartment());
                        }
                        for (UserGroup userGroup : user.getGroups()) {
                            userGroups.add(userGroup);
                        }
    	    		}
    	    		result.put("ORGUNITS", divisions);

    	    		Set<String> uDivKeys = new HashSet<String>();
    	    		for(Division div:divisions) {
    	    			uDivKeys.add(div.getKey());
    	    		}
    	    		result.put("ORGUNIT_KEYS", uDivKeys);

                    Set<String> userGroupKeys = new HashSet<String>();
                    for(UserGroup userGroup : userGroups) {
                        userGroupKeys.add(userGroup.getKey());
                    }
                    result.put("USERGROUP_KEYS", userGroupKeys);

    	    		// add to cache
    	    		userAccessCache.put(user.getNosqlId(), result);
    		}
    		return result;
    }

    @Transactional
    @Override
    public void onApplicationEvent(AbstractUMAuthenticationEvent event) {
        String appSecret = event.getAppSecret();
        Application application = applicationRepository.findByAppSecret(appSecret);
        if (event instanceof UmAuthenticationSuccessEvent) {
            UmAuthenticationSuccessEvent successEvent = (UmAuthenticationSuccessEvent) event;
            unlockUser(successEvent.getAuthentication().getName());
            updateLogin(successEvent);
        } else if (event instanceof AbstractUMAuthenticationFailureEvent) {
            String username = event.getAuthentication().getName();
            String origin = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes()).getRequest()
                    .getRemoteAddr();
            String cause = ((UmAuthenticationFailureBadCredentialsEvent) event).getException().toString();
            LOGGER.info("Failed authentication for user '{}' from ip {} caused by {}", username, origin, cause);
            if(application.isEnableUserLockCheck()) {
                if (event instanceof UmAuthenticationFailureBadCredentialsEvent) {
                    if (isUserLockedOut(username, application.getFailedLoginAttempts(), application.getLockoutTime())) {
                        updateFailedLogin(username, event.getTimestamp());
                    }
                }
            }
        }
    }
}
