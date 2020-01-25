package com.ideyatech.opentides.um.repository.cb;

import com.couchbase.client.java.query.N1qlQuery;
import com.ideyatech.opentides.core.repository.BaseEntityRepositoryHelper;
import com.ideyatech.opentides.core.repository.cb.BaseEntityRepositoryHelperCbImpl;
import com.ideyatech.opentides.um.entity.Division;
import com.ideyatech.opentides.um.repository.DivisionRepository;
import com.ideyatech.opentides.um.repository.projections.DivisionProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.couchbase.repository.query.StringN1qlBasedQuery;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author jpereira on 3/20/2017.
 */
@RepositoryRestResource(path = "/division", excerptProjection = DivisionProjection.class)
@N1qlPrimaryIndexed
public interface DivisionCbRepository extends DivisionRepository<String>{

}

class DivisionCbRepositoryCustomImpl {

    @Autowired
    private BaseEntityRepositoryHelper baseEntityRepositoryHelper;

    public List<Division> findAll(){
        CouchbaseOperations couchbaseOperations =
                ((BaseEntityRepositoryHelperCbImpl)baseEntityRepositoryHelper).getCouchbaseOperations();
        StringN1qlBasedQuery.N1qlSpelValues spelValues = StringN1qlBasedQuery.createN1qlSpelValues(couchbaseOperations.getCouchbaseBucket().name(),
                couchbaseOperations.getConverter().getTypeKey(), Division.class, false);
        String n1ql = spelValues.selectEntity + " where " + spelValues.filter + " order by parent";
        N1qlQuery query = N1qlQuery.simple(n1ql);

        return couchbaseOperations.findByN1QL(query, Division.class);
    }
}

