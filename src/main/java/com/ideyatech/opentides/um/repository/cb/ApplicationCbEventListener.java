package com.ideyatech.opentides.um.repository.cb;

import com.ideyatech.opentides.um.entity.Application;
import org.springframework.data.couchbase.core.mapping.CouchbaseDocument;
import org.springframework.data.couchbase.core.mapping.event.AbstractCouchbaseEventListener;

/**
 * Created by Gino on 9/22/2016.
 */
public class ApplicationCbEventListener extends AbstractCouchbaseEventListener<Application> {

    @Override
    public void onBeforeSave(Application source, CouchbaseDocument doc) {
        if(source.getAllowFbLogin() == null) {
            source.setAllowFbLogin(false);
        }
        if(source.getUseLdap() == null) {
            source.setUseLdap(false);
        }
        if(source.getFailedLoginAttempts() == null) {
            source.setFailedLoginAttempts(-1);
        }
        if(source.getSendActivationEmail() == null) {
            source.setSendActivationEmail(false);
        }
        if(source.getLockoutTime() == null) {
            //Default is 15 minutes
            source.setLockoutTime(15l * 60l);
        }
    }
}
