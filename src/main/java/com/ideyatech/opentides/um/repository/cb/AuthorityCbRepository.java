package com.ideyatech.opentides.um.repository.cb;

import com.couchbase.client.java.query.N1qlQuery;
import com.ideyatech.opentides.core.repository.BaseEntityRepositoryHelper;
import com.ideyatech.opentides.core.repository.cb.BaseEntityRepositoryHelperCbImpl;
import com.ideyatech.opentides.um.entity.Authority;
import com.ideyatech.opentides.um.repository.AuthorityRepository;
import com.ideyatech.opentides.um.repository.projections.AuthorityProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.data.couchbase.repository.query.StringN1qlBasedQuery;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by Gino on 8/30/2016.
 */
@RepositoryRestResource(path = "/authority", excerptProjection = AuthorityProjection.class)
public interface AuthorityCbRepository extends AuthorityRepository<String> {

}

class AuthorityCbRepositoryCustomImpl {

    @Autowired
    private BaseEntityRepositoryHelper baseEntityRepositoryHelper;

    public List<Authority> findAll(){
        CouchbaseOperations couchbaseOperations =
                ((BaseEntityRepositoryHelperCbImpl)baseEntityRepositoryHelper).getCouchbaseOperations();
        StringN1qlBasedQuery.N1qlSpelValues spelValues = StringN1qlBasedQuery.createN1qlSpelValues(couchbaseOperations.getCouchbaseBucket().name(),
                couchbaseOperations.getConverter().getTypeKey(), Authority.class, false);
        String n1ql = spelValues.selectEntity + " where  javaClass = \"com.ideyatech.opentides.um.entity.Authority\"";
        N1qlQuery query = N1qlQuery.simple(n1ql);

        return couchbaseOperations.findByN1QL(query, Authority.class);
    }
}

