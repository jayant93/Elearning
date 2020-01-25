package com.ideyatech.opentides.um.controller;

import javax.servlet.http.HttpServletRequest;

import com.ideyatech.opentides.um.service.RecaptchaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vbarcinal on 07/12/2018.
 */

@RestController
public class RecaptchaRestController {

    @Autowired
    RecaptchaService recaptchaService;

    @Transactional
    @RequestMapping(value = "/api/recaptcha", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> verify(
            @RequestParam(name="response", defaultValue = "") String recaptchaResponse, HttpServletRequest request){

        String ip = request.getRemoteAddr();
        String captchaVerifyMessage = recaptchaService.verifyRecaptcha(ip, recaptchaResponse);
        Map<String, Object> response = new HashMap<>();
        
        if (StringUtils.isNotEmpty(captchaVerifyMessage)) {
            response.put("message", captchaVerifyMessage);
            return ResponseEntity.badRequest()
                    .body(response);
        }
        
        response.put("message", "Recaptcha Successful.");
        
        return ResponseEntity.ok(response);
    }
}
