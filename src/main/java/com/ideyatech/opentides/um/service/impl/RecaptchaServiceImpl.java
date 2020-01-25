package com.ideyatech.opentides.um.service.impl;

import com.ideyatech.opentides.um.service.RecaptchaService;
import com.ideyatech.opentides.um.util.RecaptchaUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author vbarcinal on 07/12/2018.
 */

@Service
public class RecaptchaServiceImpl implements RecaptchaService {

    private static final Logger _log = LoggerFactory.getLogger(RecaptchaServiceImpl.class);

    @Value("${google.recaptcha.verifyUrl}")
    String recaptchaVerifyUrl;

    @Value("${google.recaptcha.secret}")
    String recaptchaSecret;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    public String verifyRecaptcha(String ip, String recaptchaResponse){
        Map<String, String> body = new HashMap<>();
        body.put("secret", recaptchaSecret);
        body.put("response", recaptchaResponse);
        body.put("remoteip", ip);
        _log.debug("Request body for recaptcha: {}", body);
        ResponseEntity<Map> recaptchaResponseEntity =
                restTemplateBuilder.build()
                        .postForEntity(recaptchaVerifyUrl +
                                        "?secret={secret}&response={response}&remoteip={remoteip}",
                                body, Map.class, body);

        _log.debug("Response from recaptcha: {}",
                recaptchaResponseEntity);
        Map<String, Object> responseBody =
                recaptchaResponseEntity.getBody();

        boolean recaptchaSuccess = (Boolean)responseBody.get("success");
        if ( !recaptchaSuccess) {
            List<String> errorCodes =
                    (List)responseBody.get("error-codes");

            String errorMessage = errorCodes.stream()
                    .map(s -> RecaptchaUtil.RECAPTCHA_ERROR_CODE.get(s))
                    .collect(Collectors.joining(", "));

            return errorMessage;
        }else {
            return StringUtils.EMPTY;
        }
    }
}
