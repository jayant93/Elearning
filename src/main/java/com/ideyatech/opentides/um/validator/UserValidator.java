package com.ideyatech.opentides.um.validator;

import com.ideyatech.opentides.core.util.StringUtil;
import com.ideyatech.opentides.core.util.ValidatorUtil;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.repository.ApplicationRepository;
import com.ideyatech.opentides.um.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator for User object
 *
 * @author Gino
 */
@Component
public class UserValidator implements Validator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PasswordValidator passwordValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return BaseUser.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors e) {

        BaseUser user = (BaseUser) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(e, "firstName",
                "error.required", new Object[]{"First Name"},"First name is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(e, "lastName",
                "error.required", new Object[]{"Last Name"},"Last name is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(e, "credential.username",
                "error.required", new Object[]{"Username"},"Username is required.");

/*        ValidationUtils.rejectIfEmptyOrWhitespace(e, "groups",
                "error.required.at-least-one", new Object[]{"Groups"},"At least one Usergroup is required.");
*/
        if (isDuplicateUsername(user)) {
            e.rejectValue("username", "error.duplicate-field", new Object[]{user.getCredential().getUsername(), "username"}, "User name already exists.");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(e, "emailAddress",
                "error.required", new Object[]{"Email Address"},"Email address is required.");
        if(!StringUtil.isEmpty(user.getEmailAddress())){
            if (!ValidatorUtil.isEmail(user.getEmailAddress())) {
                e.rejectValue("emailAddress", "error.invalid-email-address",new Object[]{user.getEmailAddress()},"Email Address is invalid.");
            }
            if (isDuplicateEmail(user)) {
                e.rejectValue("emailAddress","error.duplicate-field", new Object[]{user.getEmailAddress(),"email"}, "Email address already exists.");
            }
        }

/*        if (user.getIsNew()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(e, "credential.newPassword",
                    "error.required", new Object[]{"Password"},"Password is required.");
            ValidationUtils.rejectIfEmptyOrWhitespace(e, "credential.confirmPassword",
                    "error.required", new Object[]{"Confirm Password"},"Confirm password is required.");
        }*/

        if (!StringUtil.isEmpty(user.getCredential().getNewPassword()) &&
                !StringUtil.isEmpty(user.getCredential().getConfirmPassword()) &&
                !user.getCredential().getNewPassword().equals(user.getCredential().getConfirmPassword())) {
            e.rejectValue("credential.confirmPassword","error.password-confirmation-did-not-match", "Password confirmation did not match.");
        }

/*        if (!passwordValidator.isPasswordValid(user.getCredential().getNewPassword(), user.getPasswordRule())) {
            e.rejectValue("credential.newPassword","error.password-combination.invalid",
                    new Object[] {ValidatorUtil.getPasswordRuleDisplay(user.getPasswordRule())},
                    "Password is invalid.");
        }*/

    }

    private boolean isDuplicateUsername(BaseUser user) {
        String userName = user.getCredential().getUsername();
        BaseUser userCheck = userRepository.findByUsername(userName);
        if (userCheck != null && user.isNew()) {
            return true;
        }
        if (userCheck != null && !userCheck.getId().equals(user.getId())) {
            return true;
        }
        return false;
    }

    private boolean isDuplicateEmail(BaseUser user) {
        String email = user.getEmailAddress();
        if (email != null && !StringUtil.isEmpty(email)){
            BaseUser userCheck = userRepository.findByEmailAddress(email);
            if (userCheck != null && user.isNew())
                return true;
            if (userCheck != null && !userCheck.getId().equals(user.getId()))
                return true;
        }
        return false;
    }

}
