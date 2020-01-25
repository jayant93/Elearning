package com.ideyatech.opentides.um.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.Division;

/**
 * @author jpereira on 3/15/2017.
 */
@NoRepositoryBean
public interface DivisionRepository<ID extends Serializable> extends BaseEntityRepository<Division, ID> {

    Division findByKey(@Param("key") String key);
    
    @Query(value = "select d from Division d where json_extract(d.parent, '$.key') = :parentKey")
    @QueryHints(value = {
        @QueryHint(value = "true", name = "org.hibernate.cacheable")})
    @org.springframework.data.couchbase.core.query.Query(value = "#{#n1ql.selectEntity} " +
            "where #{#n1ql.filter} and parent.`key` = $1")
    @org.springframework.data.mongodb.repository.Query(value = "{'parent.key' : ?0}")
    List<Division> findByParent(@Param("parentKey") String parentKey);

    @Query(value = "select d from Division d where json_extract(d.parent, '$.key') IS NULL ")
    @QueryHints(value = {
        @QueryHint(value = "true", name = "org.hibernate.cacheable")})
    @org.springframework.data.couchbase.core.query.Query(value = "#{#n1ql.selectEntity} " +
            "where #{#n1ql.filter} and parent is not valued")
    @org.springframework.data.mongodb.repository.Query(value = "{'parent.key' : null}")
    List<Division> findParentDivisions();
    
	List<Division> keywordSearchDivision(@Param("keyword") String keyword);
	
	// For advanced search queries
	List<Division> findAllByNameIgnoreCase(@Param("name") String name);
	
	List<Division> findAllByDescriptionIgnoreCase(@Param("description") String description);
	
	List<Division> findAllByKeyIgnoreCase(@Param("key") String key);
	
	List<Division> findAllByParent(@Param("parent") String parent);
	
	Page<Division> search(String type, HashMap<String, Object> criteria, Pageable page);
}