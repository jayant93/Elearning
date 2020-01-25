package com.ideyatech.opentides.um.service;

/**
 * @author vbarcinal on 07/12/2018.
 */
public interface RecaptchaService {

    String verifyRecaptcha(String ip, String recaptchaResponse);
}
