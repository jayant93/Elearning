package com.ideyatech.opentides.um.controller;

import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.entity.UserCustomField;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Gino on 9/7/2016.
 */
@BasePathAwareController
@RequestMapping(value = "/api/fields")
public class UserCustomFieldRestController extends BaseRestController<UserCustomField> {

}
