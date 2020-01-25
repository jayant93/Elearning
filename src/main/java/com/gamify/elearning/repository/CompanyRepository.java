package com.gamify.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamify.elearning.entity.Company;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;

public interface CompanyRepository extends BaseEntityRepository<Company, String> {
	
	@Query(value="select c from Company c where lower(c.name) like lower(concat('%', :name, '%'))")
	List<Company> findCompanyWithSimilarName(@Param("name") String companyName);

}
