package com.ideyatech.opentides.um.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author jpereira on 12/20/2016.
 */
@Controller
@RequestMapping("/home")
public class ApplicationController
{
    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return "/main/app.html";
    }
}
