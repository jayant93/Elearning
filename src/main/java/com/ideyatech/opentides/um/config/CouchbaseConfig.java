package com.ideyatech.opentides.um.config;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.ideyatech.opentides.core.repository.BaseEntityRepositoryHelper;
import com.ideyatech.opentides.core.repository.cb.BaseEntityCbSDREventListener;
import com.ideyatech.opentides.core.repository.cb.BaseEntityCouchbaseEventListener;
import com.ideyatech.opentides.core.repository.cb.BaseEntityRepositoryHelperCbImpl;
import com.ideyatech.opentides.core.repository.impl.BaseEntityRepositoryCbImpl;
import com.ideyatech.opentides.um.Application;
import com.ideyatech.opentides.um.repository.cb.ApplicationCbEventListener;
import com.ideyatech.opentides.um.repository.cb.BaseUserCbEventListener;
import com.ideyatech.opentides.um.repository.cb.SyncGatewayRepositoryEventListener;
import com.ideyatech.opentides.um.repository.cb.UserEventCbListener;
import com.ideyatech.opentides.um.security.UsernamePasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.config.BeanNames;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.data.couchbase.repository.support.IndexManager;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Gino on 9/21/2016.
 */
@Configuration
@Profile("cb")
@EnableCouchbaseRepositories(basePackages = {
        "com.ideyatech.opentides.core.repository.cb",
        "com.ideyatech.opentides.um.repository.cb"
}, repositoryImplementationPostfix = "CustomImpl",
   repositoryBaseClass = BaseEntityRepositoryCbImpl.class
)
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

    @Value("${cb.bucket.name}")
    private String bucketName;

    @Value("${cb.bucket.password}")
    private String bucketPassword;

    @Value("#{'${cb.host}'.split(',')}")
    private List<String> cbHosts;

    public CouchbaseConfig() {
        Application.setEntityIdType(String.class);
    }

    @Bean
    public BaseEntityCouchbaseEventListener baseEntityCouchbaseEventListener() {
        return new BaseEntityCouchbaseEventListener();
    }

    @Bean
    public ApplicationCbEventListener applicationCbEventListener() {
        return new ApplicationCbEventListener();
    }

    @Bean
    public BaseEntityRepositoryHelper baseEntityRepositoryHelper() {
        return new BaseEntityRepositoryHelperCbImpl();
    }

    @Override
    public MappingCouchbaseConverter mappingCouchbaseConverter() throws Exception {
        MappingCouchbaseConverter couchbaseConverter = super.mappingCouchbaseConverter();
        couchbaseConverter.setEnableStrictFieldChecking(true);
        return couchbaseConverter;
    }

    @Bean
    public UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider() {
        UsernamePasswordAuthenticationProvider authenticationProvider =
                new UsernamePasswordAuthenticationProvider();
        authenticationProvider.setLoadGroupsFromDb(true);
        return authenticationProvider;
    }

    @Override
    public String typeKey() {
        return MappingCouchbaseConverter.TYPEKEY_SYNCGATEWAY_COMPATIBLE;
    }

    @Override
    protected List<String> getBootstrapHosts() {
        return cbHosts;
    }

    @Override
    protected String getBucketName() {
        return bucketName;
    }

    @Override
    protected String getBucketPassword() {
        return bucketPassword;
    }

    @Override
    public IndexManager indexManager() {
        return new IndexManager(true, true, true);
    }

    @Bean
    public SyncGatewayRepositoryEventListener syncGatewayRepositoryEventListener() {
        return new SyncGatewayRepositoryEventListener();
    }

    @Bean
    public UserEventCbListener userEventCbListener() {
        return new UserEventCbListener();
    }

    @Bean
    public BaseUserCbEventListener baseUserCbEventListener() {
        return new BaseUserCbEventListener();
    }

    @Bean
    public BaseEntityCbSDREventListener baseEntityCbSDREventListener() {
        return new BaseEntityCbSDREventListener();
    }
}
