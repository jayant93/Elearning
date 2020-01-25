package com.ideyatech.opentides.um.repository.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ideyatech.opentides.um.entity.Division;
import com.ideyatech.opentides.um.entity.UserGroup;
import com.ideyatech.opentides.um.repository.DivisionRepository;

@RepositoryRestResource(path = "/division")
public interface DivisionJpaRepository extends DivisionRepository<String> {

	List<Division> findByNameContaining(@Param("name") String name);
	
	@Query(value="select * from division where lower(json_extract(parent, '$.name')) like %:keyword% or "
			+ "name like %:keyword% or description like %:keyword% or key_ like %:keyword%", nativeQuery = true)
	List<Division> keywordSearchDivision(@Param("keyword") String keyword);
		
	@Query(value="select * from division where lower(json_extract(parent, '$.name')) like lower(json_quote(:parent))",
			nativeQuery = true)
	List<Division> findAllByParent(@Param("parent") String parent);
	
	Page<Division> search(String type, HashMap<String, Object> criteria, Pageable page);
}

class DivisionJpaRepositoryCustomImpl {
    
    @Autowired
    private DivisionRepository divisionRepository;

    public Page<Division> search(String type, HashMap<String, Object> criteria, Pageable page) {
    	List<Division> divisions = new ArrayList<Division>();
    	HashMap<String, List<Division>> mapList = new HashMap<String, List<Division>>();
    	Set<Division> divisionSet = new HashSet<Division>();
    	
    	if (type.equals("keyword")) {
    		divisions = divisionRepository.keywordSearchDivision(criteria.get("keyword").toString());
    	} else {
    		HashMap<String, String> keyValue = new HashMap<String, String>();
        	Iterator it = ((HashMap<String, Object>) criteria.get("criteria")).entrySet().iterator();
        	int ctr = 0;
        	
        	while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if (pair.getValue() != null && !StringUtils.isBlank(pair.getValue().toString())) {
                	List<Division> tempDivision = new ArrayList<Division>();
                	
                	if (pair.getKey().toString().equals("name")) {
                		tempDivision = divisionRepository.findAllByNameIgnoreCase(pair.getValue().toString());
                	} else if (pair.getKey().toString().equals("description")) {
                		tempDivision = divisionRepository.findAllByDescriptionIgnoreCase(pair.getValue().toString());
                	} else if (pair.getKey().toString().equals("key")) {
                		tempDivision = divisionRepository.findAllByKeyIgnoreCase(pair.getValue().toString());
                	} else if (pair.getKey().toString().equals("parent")) {
                		tempDivision = divisionRepository.findAllByParent(pair.getValue().toString());
                	}
                	
                	if (ctr == 0) {
                		divisionSet.addAll(tempDivision);
                	} else {
                		divisionSet.retainAll(tempDivision);
                	}
                	ctr++;
                }
                it.remove(); // avoids a ConcurrentModificationException
            }
        	
        	divisions.addAll(divisionSet);
    	}
    	
    	// Create and return user's data with pagination
        PagedListHolder<Division> tempDivision = new PagedListHolder<Division>(divisions);
        MutableSortDefinition sortDefinition = new MutableSortDefinition("name", true, true);
        tempDivision.setSort(sortDefinition);
        tempDivision.resort();
        tempDivision.setPage(page.getPageNumber());
        tempDivision.setPageSize(page.getPageSize());
        
        Page<Division> paginatedUserList = new PageImpl<Division>(tempDivision.getPageList(), page, tempDivision.getSource().size());
        
    	return paginatedUserList;
    }
}
