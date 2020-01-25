package com.ideyatech.opentides.um.controller;

import com.gamify.elearning.entity.Company;
import com.gamify.elearning.repository.CompanyRepository;
import com.gamify.elearning.repository.ELearningUserRepository;
import com.gamify.elearning.service.ElearningFileUploadService;
import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.repository.UserCustomFieldRepository;
import com.ideyatech.opentides.um.repository.UserGroupRepository;
import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * @author johanna@ideyatech.com
 *
 */

@BasePathAwareController
@RequestMapping(value = "/api/company")
public class CompanyController extends BaseRestController<Company> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;


    @Autowired
    private UserCustomFieldRepository userCustomFieldRepository;
    
    @Autowired
	private ELearningUserRepository eLearningUserRepository;
    
    @Autowired
    private ElearningFileUploadService elearningFileUploadService;
    
    @Autowired
	private CompanyRepository companyRepository;

   @GetMapping("/findAll")
    public @ResponseBody ResponseEntity<?> getCompanies() {
        if (SecurityUtil.currentUserHasPermission("ACCESS_ALL"))
            return ResponseEntity.ok(companyRepository.findAll());
        else {
            // return only list of user's division
            List<Company> result = (List<Company>) companyRepository.findAll();
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> getCompanyById(@PathVariable String id) {
       System.out.println(id);
        return ResponseEntity.ok(companyRepository.findOne(id));
    }

    @PostMapping("/add")
    public @ResponseBody ResponseEntity<?> createCompany(@RequestBody Company company, HttpServletRequest request) {
        Company success = companyRepository.save(company);
        return ResponseEntity.ok(success);
    }

}
