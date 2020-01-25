package com.ideyatech.opentides.um.persistence;

import com.ideyatech.opentides.core.persistence.AuditableEntityHelperJpaImpl;
import com.ideyatech.opentides.um.entity.SessionUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by Gino on 9/14/2016.
 */
@Component
public class UmAuditableEntityHelperJpaImpl extends AuditableEntityHelperJpaImpl {

    private static final Logger _log = LoggerFactory.getLogger(UmAuditableEntityHelperJpaImpl.class);

    @Override
    public String getUserId() {
        try {
            final Object userObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userObj instanceof SessionUser) {
                return ((SessionUser) userObj).getId();
            }
        } catch (final NullPointerException npe) {
            _log.warn("No Security Context Found!");
        } catch (final Exception e) {
            _log.error(e.getMessage());
        }
        return "";
    }
}
