package com.ideyatech.opentides.um.controller;

import com.ideyatech.opentides.core.exception.InvalidImplementationException;
import com.ideyatech.opentides.core.util.JwtUtil;
import com.ideyatech.opentides.um.*;
import com.ideyatech.opentides.um.entity.*;
import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.repository.ApplicationRepository;
import com.ideyatech.opentides.um.repository.PasswordRulesRepository;
import com.ideyatech.opentides.um.repository.UserGroupRepository;
import com.ideyatech.opentides.um.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.AfterSaveEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeSaveEvent;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Custom Rest Controller for {@link Application} entity
 *
 * @author Gino
 */
@RepositoryRestController
public class ApplicationRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRestController.class);

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private PasswordRulesRepository passwordRulesRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Transactional
    @RequestMapping(value = "/application/{id}", method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<?> updateApplication(@PathVariable String id, @RequestBody Resource<Application> application)
            throws NoSuchAlgorithmException {

        Application inDb;
        //TODO very ugly implementation. Find a better way.
        if(com.ideyatech.opentides.um.Application.getEntityIdType().equals(Long.class)) {
            inDb = (Application)applicationRepository.findOne(Long.valueOf(id));
        } else if(com.ideyatech.opentides.um.Application.getEntityIdType().equals(String.class)) {
            inDb = (Application)applicationRepository.findOne(id);
        } else {
            throw new InvalidImplementationException(String.format("ID type %s not yet supported",
                    com.ideyatech.opentides.um.Application.getEntityIdType().toString()));
        }

        publisher.publishEvent(new BeforeSaveEvent(inDb));
        BeanUtils.copyProperties(application, inDb,
                "name", "adminUser", "passwordRules", "appSecret",
                "id", "nosqlId", "createDate", "updateDate", "createdBy", "dbName");

        applicationRepository.save(inDb);
        publisher.publishEvent(new AfterSaveEvent(inDb));
        //Build the response
        Map<String, Object> result = new HashMap<>();
        result.put("message", "SUCCESS");

        Resource<Map<String, Object>> resource = new Resource<>(result);

        resource.add(linkTo(methodOn(ApplicationRestController.class).updateApplication(id, new Resource<>(inDb))).withSelfRel());

        resource.add(new Link("/application/" + id, "application"));

        return ResponseEntity.ok(resource);

    }

    @Transactional
    @RequestMapping(value = "/application", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> registerApplication(@RequestBody Application application) throws NoSuchAlgorithmException {
        //Generate Secret Key...
        String secretKey = JwtUtil.generateSecretKey();
        application.setAppSecret(secretKey);

        //Generate Unique Link
        String link = JwtUtil.generateSecretKey(10);
        application.setLink(link);

        LOGGER.debug("Publishing BeforeCreateEvent with thread ID {}", Thread.currentThread().getId());
        publisher.publishEvent(new BeforeCreateEvent(application));

        //Save the application...
        Application savedApp = applicationRepository.save(application);

        //Create admin usergroup...
        UserGroup userGroup = userGroupRepository.setupAdminGroup(savedApp);
        savedApp.setAdminUserGroup(userGroup);

        //Create admin user...
        BaseUser adminUser = userRepository.createAdminUser(savedApp);
        savedApp.setAdminUser(adminUser);

        //Create password rules...
        PasswordRules rules = new PasswordRules();
        rules.setApplication(application);
        passwordRulesRepository.save(rules);

        //Save the application again...
        applicationRepository.save(savedApp);

        publisher.publishEvent(new AfterCreateEvent(application));

        //Build the response
        ApplicationRegistrationResponse response = new ApplicationRegistrationResponse(savedApp);

        Resource<ApplicationRegistrationResponse> resource = new Resource<>(response);

        resource.add(linkTo(methodOn(ApplicationRestController.class).registerApplication(application)).withSelfRel());

        resource.add(new Link("/application/" + savedApp.getId(), "application"));

        return ResponseEntity.ok(resource);
    }

}
