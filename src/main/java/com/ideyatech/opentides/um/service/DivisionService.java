package com.ideyatech.opentides.um.service;

import java.util.List;

import com.ideyatech.opentides.um.entity.Division;

/**
 * @author allanctan.
 */
public interface DivisionService {
	
    /**
     * returns the list of children of the given division.
     * @param rootDivision
     * @return
     */
	List<Division> findDescendants(Division rootDivision);

    /**
     * returns the list of children of the given division key.
     * @param rootDivision
     * @return
     */
	List<Division> findDescendants(String divisionKey);
}
