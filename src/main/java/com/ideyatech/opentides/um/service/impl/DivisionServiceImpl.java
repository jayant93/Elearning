package com.ideyatech.opentides.um.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ideyatech.opentides.um.entity.Division;
import com.ideyatech.opentides.um.repository.DivisionRepository;
import com.ideyatech.opentides.um.service.DivisionService;

/**
 * @author allanctan
 */
@Service
public class DivisionServiceImpl implements DivisionService {

    private static final Logger _log = LoggerFactory.getLogger(DivisionServiceImpl.class);

    @Autowired
    private DivisionRepository divisionRepository;

	@Override
	public List<Division> findDescendants(Division rootDivision) {
		List<Division> descendants = new ArrayList<Division>();
		if (rootDivision != null){
			descendants.addAll(divisionRepository.findByParent(rootDivision.getKey()));
			Division rootDivision2 = divisionRepository.findByKey(rootDivision.getKey());
			List<Division> gChildren = new ArrayList<Division>(); 
			for (Division child:descendants) {
				gChildren.addAll(findDescendants(child));
			}
			descendants.addAll(gChildren);		
			descendants.add(rootDivision2);
		}
		return descendants;
	}
    
	@Override
	public List<Division> findDescendants(String divisionKey) {
		Division rootDivision = divisionRepository.findByKey(divisionKey);
		return findDescendants(rootDivision);
	}
}
